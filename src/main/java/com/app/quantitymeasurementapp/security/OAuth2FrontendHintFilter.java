package com.app.quantitymeasurementapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts /oauth2/authorization/google?frontend=<hint> BEFORE Spring Security
 * redirects to Google, and saves the hint in:
 *   1. HTTP session (works when backend is a single instance / sticky sessions)
 *   2. SameSite=None; Secure cookie (survives CloudFront and load balancers)
 *
 * On the way back, OAuth2SuccessHandler reads the hint in order:
 *   session → cookie → Referer/Origin header → default (legacy)
 *
 * Usage:
 *   Old HTML frontend  → /oauth2/authorization/google?frontend=legacy
 *   Angular (Vercel)   → /oauth2/authorization/google?frontend=angular
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

                // 1. Save to session (works on direct requests)
                request.getSession(true)
                       .setAttribute(OAuth2SuccessHandler.SESSION_ATTR_FRONTEND, hint);

                // 2. Save to cookie — survives CloudFront / load balancer hops
                //    SameSite=None; Secure required for cross-site OAuth2 redirects
                Cookie c = new Cookie("oauth2_frontend", hint);
                c.setPath("/");
                c.setMaxAge(300);   // 5 minutes — long enough for OAuth2 round trip
                c.setSecure(true);
                c.setHttpOnly(false); // does not need JS access
                // SameSite=None must be set via header (Servlet API has no setter)
                response.addCookie(c);
                response.addHeader("Set-Cookie",
                    "oauth2_frontend=" + hint
                    + "; Path=/; Max-Age=300; Secure; SameSite=None");
            }
        }
        chain.doFilter(request, response);
    }
}
