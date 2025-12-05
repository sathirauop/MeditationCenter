package com.isipathana.meditationcenter.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that extracts JWT from Authorization header and authenticates the request.
 * <p>
 * Flow:
 * 1. Extract "Bearer {token}" from Authorization header
 * 2. Create JwtAuthenticationToken (unauthenticated)
 * 3. Delegate to AuthenticationManager for validation
 * 4. If valid: Store authenticated token in SecurityContext
 * 5. If invalid: Clear SecurityContext and optionally handle error
 * 6. Continue filter chain
 * <p>
 * This filter runs once per request, before other security filters.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Extract JWT from Authorization header
        String jwt = extractJwtFromRequest(request);

        // If no JWT found, continue without authentication
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Create unauthenticated token
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt);

            // Authenticate (delegates to JwtAuthenticationProvider)
            var authenticatedToken = authenticationManager.authenticate(authenticationToken);

            // Store authenticated token in SecurityContext
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticatedToken);
            SecurityContextHolder.setContext(context);

        } catch (Exception e) {
            // Clear any partial authentication
            SecurityContextHolder.clearContext();

            // Log the error
            logger.warn("JWT authentication failed: " + e.getMessage());

            // For now, continue without authentication (let @PreAuthorize handle it)
            // Alternatively, you can return 401 here:
            // authenticationEntryPoint.commence(request, response, new AuthenticationException(e.getMessage()));
            // return;
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     * Expected format: "Authorization: Bearer {token}"
     *
     * @param request HTTP request
     * @return JWT token string, or null if not present
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
