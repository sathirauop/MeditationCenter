package com.isipathana.meditationcenter.config;

import com.isipathana.meditationcenter.security.JwtAccessDeniedHandler;
import com.isipathana.meditationcenter.security.JwtAuthenticationEntryPoint;
import com.isipathana.meditationcenter.security.jwt.JwtAuthenticationFilter;
import com.isipathana.meditationcenter.security.jwt.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication.
 * <p>
 * Key Features:
 * - Stateless session management (no cookies)
 * - JWT authentication via Authorization header
 * - Role-based and permission-based authorization
 * - CORS enabled (configure as needed)
 * - CSRF disabled (not needed for stateless JWT)
 * <p>
 * Public Endpoints (no authentication required):
 * - POST /api/auth/login
 * - POST /api/auth/register
 * - POST /api/auth/refresh
 * - GET /api/programs (view programs)
 * - GET /api/events (view events)
 * <p>
 * Protected Endpoints (authentication required):
 * - All other /api/** endpoints
 * <p>
 * Method-level security enabled via @PreAuthorize:
 * - @PreAuthorize("hasRole('ADMIN')")
 * - @PreAuthorize("hasAuthority('DELETE_PROGRAM')")
 * - @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId)")
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize, @Secured, @RolesAllowed
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    /**
     * Configure HTTP security.
     * Defines which endpoints are public, which require authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT authentication)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/programs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/programs/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/{id}").permitAll()

                        // Error endpoint (used by GlobalErrorController)
                        .requestMatchers("/error").permitAll()

                        // All other /api/** endpoints require authentication
                        .requestMatchers("/api/**").authenticated()

                        // Allow all other requests (for now)
                        .anyRequest().permitAll()
                )

                // Stateless session management (no sessions, no cookies)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // 401 Unauthorized
                        .accessDeniedHandler(accessDeniedHandler)           // 403 Forbidden
                )

                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Create JWT authentication filter bean.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(
                authenticationManager(),
                authenticationEntryPoint
        );
    }

    /**
     * Create authentication manager with JWT authentication provider.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(jwtAuthenticationProvider);
    }

    /**
     * Password encoder for hashing passwords.
     * Uses BCrypt with default strength (10).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
