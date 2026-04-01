package com.app.quantitymeasurementapp.security;

import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Called by Spring Security after a successful Google OAuth2 login.
 *
 * Flow:
 *   1. Extract Google profile attributes from OAuth2User
 *   2. Find or create the User record in the DB
 *   3. Issue a JWT
 *   4. Redirect to /oauth2-callback.html?token=<jwt>
 *      The static page extracts the token, saves it to localStorage,
 *      and navigates the user to operations.html.
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    // Frontend callback page served from Spring Boot's static resources
    private static final String FRONTEND_CALLBACK = "/oauth2-callback.html";

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

        String googleId = oAuth2User.getAttribute("sub");
        String email    = oAuth2User.getAttribute("email");
        String name     = oAuth2User.getAttribute("name");

        String firstName = name != null ? name.split(" ")[0]                          : "Google";
        String lastName  = name != null && name.contains(" ")
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
                            .firstName(firstName)
                            .lastName(lastName)
                            .email(email)
                            .googleId(googleId)
                            .authProvider(User.AuthProvider.AUTH_GOOGLE)
                            .role(User.Role.USER)
                            .build()
                ));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        log.info("OAuth2 login success for {}", email);

        // Redirect to the static frontend page; it reads ?token= from the URL,
        // stores it in localStorage, and navigates to operations.html.
        getRedirectStrategy().sendRedirect(request, response,
                FRONTEND_CALLBACK + "?token=" + token);
    }
}
