package com.app.quantitymeasurementapp.util;

import com.app.quantitymeasurementapp.security.JwtAuthFilter;
import com.app.quantitymeasurementapp.security.OAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter        jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final UserDetailsService   userDetailsService;

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(JwtAuthFilter        jwtAuthFilter,
                          OAuth2SuccessHandler  oAuth2SuccessHandler,
                          UserDetailsService    userDetailsService) {
        this.jwtAuthFilter        = jwtAuthFilter;
        this.oAuth2SuccessHandler  = oAuth2SuccessHandler;
        this.userDetailsService    = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of(
            "http://localhost:*", "http://127.0.0.1:*",
            "https://anubhav-03042004.github.io"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

            .authorizeHttpRequests(auth -> auth

                // Static frontend assets — including new password-reset pages
                .requestMatchers(
                    "/", "/index.html", "/login.html", "/register.html",
                    "/operations.html", "/dashboard.html", "/profile.html",
                    "/oauth2-callback.html",
                    "/forgot-password.html", "/reset-password.html",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico", "/*.png"
                ).permitAll()

                // Auth endpoints — register, login, OAuth2, forgot/reset
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/oauth2/**",
                    "/login/oauth2/**"
                ).permitAll()

                // Dev / docs
                .requestMatchers(
                    "/h2-console/**",
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/actuator/**"
                ).permitAll()

                // Quantity operations — public
                .requestMatchers(
                    "/api/v1/quantities/compare",
                    "/api/v1/quantities/convert",
                    "/api/v1/quantities/add",
                    "/api/v1/quantities/subtract",
                    "/api/v1/quantities/divide"
                ).permitAll()

                // History / counts — authenticated
                .requestMatchers(
                    "/api/v1/quantities/history/**",
                    "/api/v1/quantities/count/**"
                ).authenticated()

                .requestMatchers("/api/v1/users/**").authenticated()
                .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
            );

        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth -> oauth
                    .loginPage("/oauth2/authorization/google")
                    .successHandler(oAuth2SuccessHandler));
        }

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
