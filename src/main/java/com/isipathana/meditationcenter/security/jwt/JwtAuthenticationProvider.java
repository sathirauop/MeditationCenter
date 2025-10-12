package com.isipathana.meditationcenter.security.jwt;

import com.isipathana.meditationcenter.exception.AuthenticationException;
import com.isipathana.meditationcenter.repository.UserRepository;
import com.isipathana.meditationcenter.security.MeditationCenterUser;
import com.isipathana.meditationcenter.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Authentication provider that validates JWT tokens.
 * <p>
 * Flow:
 * 1. Receive JwtAuthenticationToken (unauthenticated) from filter
 * 2. Extract JWT string
 * 3. Validate JWT signature and expiration (via JwtService)
 * 4. Extract userId, email, role from JWT claims
 * 5. (Optional) Load user from database to verify account is still active
 * 6. Create MeditationCenterUser principal
 * 7. Return authenticated JwtAuthenticationToken
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) {
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        String jwt = jwtAuth.getJwt();

        // Validate JWT
        if (!jwtService.validateToken(jwt)) {
            throw new AuthenticationException("Invalid or expired JWT token");
        }

        // Check token type (must be ACCESS token, not REFRESH)
        String tokenType = jwtService.extractTokenType(jwt);
        if (!"ACCESS".equals(tokenType)) {
            throw new AuthenticationException("Invalid token type. Expected ACCESS token.");
        }

        // Extract user information from JWT
        Long userId = jwtService.extractUserId(jwt);
        String email = jwtService.extractEmail(jwt);
        Role role = jwtService.extractRole(jwt);

        // Load user from database to verify account is still active
        var user = userRepository.findById(userId);

        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        if (user.isActive() == null || !user.isActive()) {
            throw new AuthenticationException("User account is inactive");
        }

        // Create authenticated principal
        MeditationCenterUser principal = MeditationCenterUser.builder()
                .userId(userId)
                .email(email)
                .name(user.name())
                .role(role)
                .isActive(user.isActive())
                .emailVerified(user.emailVerified())
                .build();

        // Return authenticated token
        return new JwtAuthenticationToken(jwt, principal, principal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
