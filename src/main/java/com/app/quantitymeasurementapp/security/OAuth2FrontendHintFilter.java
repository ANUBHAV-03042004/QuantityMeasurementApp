package com.app.quantitymeasurementapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Saves the ?frontend= query param into the HTTP session BEFORE Spring Security
 * redirects the browser to Google. OAuth2SuccessHandler reads it on the way back
 * to decide which frontend callback URL to redirect the JWT to.
 *
 * Old HTML frontend  → /oauth2/authorization/google?frontend=legacy
 * New Angular Azure  → /oauth2/authorization/google?frontend=angular
 */
@Component
public class OAuth2FrontendHintFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    FilterChain         chain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/oauth2/authorization/")) {
            String hint = request.getParameter("frontend");
            if (hint != null && !hint.isBlank()) {
                request.getSession(true)
                       .setAttribute(OAuth2SuccessHandler.SESSION_ATTR_FRONTEND, hint);
            }
        }
        chain.doFilter(request, response);
    }
}
