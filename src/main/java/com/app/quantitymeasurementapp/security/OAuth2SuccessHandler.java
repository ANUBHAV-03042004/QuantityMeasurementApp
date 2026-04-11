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

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);
    public static final String SESSION_ATTR_FRONTEND = "oauth2_frontend";

    @Value("${app.oauth2.legacy-callback:https://anubhav-03042004.github.io/QuantityMeasurementApp-Frontend/oauth2-callback.html}")
    private String legacyCallback;

    @Value("${app.oauth2.angular-callback:https://quantra-angular.azurewebsites.net/oauth2-callback}")
    private String angularCallback;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

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

        // Try session first
        String hint = null;
        if (request.getSession(false) != null) {
            Object o = request.getSession(false).getAttribute(SESSION_ATTR_FRONTEND);
            if (o != null) hint = o.toString();
        }

        // Fallback to cookie if session was lost (e.g. via CloudFront)
        if (hint == null && request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("oauth2_frontend".equals(c.getName())) {
                    hint = c.getValue();
                    // Clear the cookie
                    Cookie clear = new Cookie("oauth2_frontend", "");
                    clear.setPath("/");
                    clear.setMaxAge(0);
                    response.addCookie(clear);
                    break;
                }
            }
        }

        log.info("OAuth2 frontend hint: {}", hint);
        String callbackBase = "angular".equalsIgnoreCase(hint) ? angularCallback : legacyCallback;
        getRedirectStrategy().sendRedirect(request, response, callbackBase + "?token=" + token);
    }
}