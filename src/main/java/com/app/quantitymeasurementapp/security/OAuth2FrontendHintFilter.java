package com.app.quantitymeasurementapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OAuth2FrontendHintFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/oauth2/authorization/")) {
            String hint = request.getParameter("frontend");
            if (hint != null && !hint.isBlank()) {
                // Store in BOTH session and cookie so CloudFront doesn't lose it
                request.getSession(true)
                       .setAttribute(OAuth2SuccessHandler.SESSION_ATTR_FRONTEND, hint);
                // Also store in cookie as fallback
                jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("oauth2_frontend", hint);
                cookie.setPath("/");
                cookie.setMaxAge(300); // 5 minutes
                cookie.setSecure(true);
                response.addCookie(cookie);
            }
        }
        chain.doFilter(request, response);
    }
}