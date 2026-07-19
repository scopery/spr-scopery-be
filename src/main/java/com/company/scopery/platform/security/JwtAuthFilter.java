package com.company.scopery.platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX     = "Bearer ";
    private static final String AUTHORIZATION_HDR = "Authorization";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null && jwtService.isTokenValid(token)) {
            String username = jwtService.extractUsername(token);
            boolean portal = jwtService.isPortalToken(token);
            var authority = new SimpleGrantedAuthority(portal ? "ROLE_PORTAL_USER" : "ROLE_USER");
            var auth = new UsernamePasswordAuthenticationToken(username, null, List.of(authority));
            auth.setDetails(MapTokenDetails.of(jwtService.extractUserId(token), jwtService.extractPrincipalType(token)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HDR);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> CookieUtil.ACCESS_TOKEN_COOKIE.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public record MapTokenDetails(java.util.UUID subjectId, String principalType) {
        public static MapTokenDetails of(java.util.UUID subjectId, String principalType) {
            return new MapTokenDetails(subjectId, principalType);
        }
    }
}
