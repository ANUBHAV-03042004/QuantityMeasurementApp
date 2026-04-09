package com.app.quantitymeasurementapp.security;

import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
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
 * After successful Google OAuth2 login:
 *  1. Finds or creates the user in DB.
 *  2. Generates a JWT.
 *  3. Redirects to the correct frontend's /oauth2-callback, appending ?token=<jwt>
 *
 * Which frontend to redirect to is determined by the "frontend" query param that
 * each frontend passes when starting the OAuth2 flow (saved to session by
 * OAuth2FrontendHintFilter before Google redirect):
 *
 *   Old HTML → /oauth2/authorization/google?frontend=legacy  → LEGACY_CALLBACK
 *   Angular  → /oauth2/authorization/google?frontend=angular → ANGULAR_CALLBACK
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    public static final String SESSION_ATTR_FRONTEND = "oauth2_frontend";

    /** Old HTML/JS frontend on GitHub Pages */
    @Value("${app.oauth2.legacy-callback:https://anubhav-03042004.github.io/QuantityMeasurementApp-Frontend/oauth2-callback.html}")
    private String legacyCallback;

    /**
     * New Angular dragon frontend on Azure.
     * *** Replace with your actual Azure Static Web App / App Service URL ***
     * e.g. https://quantra-angular.azurewebsites.net/oauth2-callback
     *
     * The Angular route has no .html extension because Angular handles routing
     * internally via the catch-all redirect in staticwebapp.config.json.
     */
    @Value("${app.oauth2.angular-callback:https://quantra-angular.azurewebsites.net/oauth2-callback}")
    private String angularCallback;

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

        // Read which frontend initiated this login from the session
        String hint = null;
        if (request.getSession(false) != null) {
            Object o = request.getSession(false).getAttribute(SESSION_ATTR_FRONTEND);
            if (o != null) hint = o.toString();
        }

        String callbackBase = "angular".equalsIgnoreCase(hint) ?angularCallback :  legacyCallback ;
        getRedirectStrategy().sendRedirect(request, response, callbackBase + "?token=" + token);
    }
}
