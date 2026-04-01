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

    // Injected only when spring.security.oauth2.client.registration.google.*
    // properties are present; null otherwise (app starts fine without them).
    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(JwtAuthFilter        jwtAuthFilter,
                          OAuth2SuccessHandler  oAuth2SuccessHandler,
                          UserDetailsService    userDetailsService) {
        this.jwtAuthFilter        = jwtAuthFilter;
        this.oAuth2SuccessHandler  = oAuth2SuccessHandler;
        this.userDetailsService    = userDetailsService;
    }

    // ── Password encoder ──────────────────────────────────────────────────────

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Auth provider ─────────────────────────────────────────────────────────

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── Auth manager ──────────────────────────────────────────────────────────

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── CORS ──────────────────────────────────────────────────────────────────
    // Allows the frontend (served from the same origin on port 8080, or from a
    // local dev server on a different port) to call the API.

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*","https://anubhav-03042004.github.io"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    // ── Security filter chain ─────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)

            // IF_REQUIRED (not STATELESS) so that the OAuth2 flow can store its
            // state parameter in the HTTP session between the authorization
            // redirect and the callback.  The JWT filter handles authentication
            // for every API call independently, so sessions are never used for
            // API auth regardless of this setting.
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // Allow H2 console frames
            .headers(headers ->
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

            .authorizeHttpRequests(auth -> auth

                // ── Static frontend assets ────────────────────────────────────
                .requestMatchers(
                    "/", "/index.html", "/login.html", "/register.html",
                    "/operations.html", "/dashboard.html", "/profile.html",
                    "/oauth2-callback.html",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico"
                ).permitAll()

                // ── Auth endpoints ────────────────────────────────────────────
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/oauth2/**",
                    "/login/oauth2/**"
                ).permitAll()

                // ── Dev / docs endpoints ──────────────────────────────────────
                .requestMatchers(
                    "/h2-console/**",
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/actuator/**"
                ).permitAll()

                // ── Quantity operations — PUBLIC (guest + user) ───────────────
                // Anyone may run calculations.  History is saved to the DB for
                // every request, but the history/count read endpoints are
                // protected so only authenticated users can fetch it.
                .requestMatchers(
                    "/api/v1/quantities/compare",
                    "/api/v1/quantities/convert",
                    "/api/v1/quantities/add",
                    "/api/v1/quantities/subtract",
                    "/api/v1/quantities/divide"
                ).permitAll()

                // ── Quantity history / counts — AUTHENTICATED ─────────────────
                // Returns 401 for unauthenticated requests; the frontend shows
                // the sign-in gate instead of the table.
                .requestMatchers(
                    "/api/v1/quantities/history/**",
                    "/api/v1/quantities/count/**"
                ).authenticated()

                // ── User management ───────────────────────────────────────────
                .requestMatchers("/api/v1/users/**").authenticated()

                .anyRequest().authenticated()
            )

            // ── 401 entry point ───────────────────────────────────────────────
            // Restores the plain-401 response that oauth2Login() would otherwise
            // replace with a 302 redirect to the Google login page — wrong for
            // a REST API client.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    (request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
            );

        // ── OAuth2 login — conditional on credentials being configured ────────
        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth -> oauth
                    .loginPage("/oauth2/authorization/google")
                    .successHandler(oAuth2SuccessHandler)
            );
        }

        // ── JWT filter ────────────────────────────────────────────────────────
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
