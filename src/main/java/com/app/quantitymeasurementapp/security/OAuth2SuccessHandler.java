package com.app.quantitymeasurementapp.security;

import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * After successful Google OAuth2 login, redirects the JWT to the correct frontend.
 *
 * HINT RESOLUTION — tried in order, first non-null wins:
 *
 *   1. HTTP session    — works when CloudFront has sticky sessions / single instance
 *   2. Cookie          — set by OAuth2FrontendHintFilter; survives most CloudFront configs
 *   3. Referer/Origin  — last-resort header fallback (set by app.oauth2.angular-origin)
 *   4. Default         — always falls back to legacy (GitHub Pages)
 *
 * BOTH FRONTENDS WORK SIMULTANEOUSLY:
 *   Old HTML (GitHub Pages) → /oauth2/authorization/google?frontend=legacy
 *                           → redirected to app.oauth2.legacy-callback
 *
 *   Angular (Vercel)        → /oauth2/authorization/google?frontend=angular
 *                           → redirected to app.oauth2.angular-callback
 *
 * Set these in Elastic Beanstalk → Configuration → Environment properties:
 *   OAUTH2_LEGACY_CALLBACK  = https://anubhav-03042004.github.io/QuantityMeasurementApp-Frontend/oauth2-callback.html
 *   OAUTH2_ANGULAR_CALLBACK = https://quantity-measurement-app-frontend-tawny.vercel.app/oauth2-callback
 *   OAUTH2_ANGULAR_ORIGIN   = https://quantity-measurement-app-frontend-tawny.vercel.app
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    public static final String SESSION_ATTR_FRONTEND = "oauth2_frontend";

    /** Old HTML/JS frontend — GitHub Pages */
    @Value("${app.oauth2.legacy-callback:https://anubhav-03042004.github.io/QuantityMeasurementApp-Frontend/oauth2-callback.html}")
    private String legacyCallback;

    /**
     * Angular frontend — Vercel.
     * Env var: OAUTH2_ANGULAR_CALLBACK
     * Value:   https://quantity-measurement-app-frontend-tawny.vercel.app/oauth2-callback
     */
    @Value("${app.oauth2.angular-callback:https://quantity-measurement-app-frontend-tawny.vercel.app/oauth2-callback}")
    private String angularCallback;

    /**
     * Angular frontend origin — used for Referer/Origin header matching.
     * Env var: OAUTH2_ANGULAR_ORIGIN
     * Value:   https://quantity-measurement-app-frontend-tawny.vercel.app
     */
    @Value("${app.oauth2.angular-origin:https://quantity-measurement-app-frontend-tawny.vercel.app}")
    private String angularOrigin;

    private final JwtUtil        jwtUtil;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil        = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest  request,
                                        HttpServletResponse response,
                                        Authentication      authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String googleId  = oAuth2User.getAttribute("sub");
        String email     = oAuth2User.getAttribute("email");
        String name      = oAuth2User.getAttribute("name");
        String firstName = name != null ? name.split(" ")[0] : "Google";
        String lastName  = (name != null && name.contains(" "))
                           ? name.substring(name.indexOf(' ') + 1) : "User";

        User user = userRepository.findByGoogleId(googleId)
                .or(() -> userRepository.findByEmail(email))
                .map(existing -> {
                    if (existing.getGoogleId() == null) {
                        existing.setGoogleId(googleId);
                        return userRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                            .firstName(firstName).lastName(lastName)
                            .email(email).googleId(googleId)
                            .authProvider(User.AuthProvider.AUTH_GOOGLE)
                            .role(User.Role.USER)
                            .build()));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        log.info("OAuth2 login success for {}", email);

        String hint = resolveHint(request, response);
        log.info("OAuth2 frontend hint resolved: '{}' → {}", hint,
                 "angular".equalsIgnoreCase(hint) ? angularCallback : legacyCallback);

        String callbackBase = "angular".equalsIgnoreCase(hint) ? angularCallback : legacyCallback;
        getRedirectStrategy().sendRedirect(request, response, callbackBase + "?token=" + token);
    }

    /**
     * Try 4 layers in order — first non-null "angular" or "legacy" wins.
     */
    private String resolveHint(HttpServletRequest request, HttpServletResponse response) {

        // ── Layer 1: HTTP session ─────────────────────────────────────────────
        if (request.getSession(false) != null) {
            Object o = request.getSession(false).getAttribute(SESSION_ATTR_FRONTEND);
            if (o != null) {
                log.debug("OAuth2 hint from session: {}", o);
                return o.toString();
            }
        }

        // ── Layer 2: Cookie ───────────────────────────────────────────────────
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("oauth2_frontend".equals(c.getName()) && c.getValue() != null) {
                    String hint = c.getValue();
                    log.debug("OAuth2 hint from cookie: {}", hint);
                    // Clear the cookie immediately
                    Cookie clear = new Cookie("oauth2_frontend", "");
                    clear.setPath("/");
                    clear.setMaxAge(0);
                    clear.setSecure(true);
                    response.addCookie(clear);
                    response.addHeader("Set-Cookie",
                        "oauth2_frontend=; Path=/; Max-Age=0; Secure; SameSite=None");
                    return hint;
                }
            }
        }

        // ── Layer 3: Referer / Origin header ─────────────────────────────────
        // If the OAuth2 flow was initiated from the Angular app, the browser
        // usually sends the Angular origin in the Referer or Origin header.
        String referer = request.getHeader("Referer");
        String origin  = request.getHeader("Origin");
        String source  = referer != null ? referer : origin;
        log.debug("OAuth2 Referer/Origin header: {}", source);

        if (source != null) {
            // Strip protocol for a looser match
            String angularHost = angularOrigin.replaceFirst("https?://", "");
            if (source.replaceFirst("https?://", "").startsWith(angularHost)) {
                log.debug("OAuth2 hint from Referer/Origin: angular");
                return "angular";
            }
        }

        // ── Layer 4: Default → legacy ─────────────────────────────────────────
        log.debug("OAuth2 hint: no hint found, defaulting to legacy");
        return "legacy";
    }
}
