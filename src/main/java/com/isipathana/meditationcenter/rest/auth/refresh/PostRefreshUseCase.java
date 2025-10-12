package com.isipathana.meditationcenter.rest.auth.refresh;

import com.isipathana.meditationcenter.config.JwtProperties;
import com.isipathana.meditationcenter.exception.AuthenticationException;
import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.security.Role;
import com.isipathana.meditationcenter.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for token refresh.
 * Handles business logic for refreshing access tokens using refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class PostRefreshUseCase {

    private final PostRefreshDataAccess repository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /**
     * Execute token refresh.
     *
     * @param request Refresh request
     * @return Response with new access token
     * @throws AuthenticationException if refresh token is invalid or user is inactive
     */
    @Transactional(readOnly = true)
    public PostRefreshResponse execute(PostRefreshRequest request) {
        String refreshToken = request.refreshToken();

        // Validate refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthenticationException("Invalid or expired refresh token");
        }

        // Check token type
        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            throw new AuthenticationException("Invalid token type. Expected REFRESH token.");
        }

        // Extract user info
        Long userId = jwtService.extractUserId(refreshToken);
        String email = jwtService.extractEmail(refreshToken);

        // Load user from database to verify account is still active
        User user = repository.findById(userId);

        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        if (user.isActive() == null || !user.isActive()) {
            throw new AuthenticationException("Account is inactive");
        }

        // Convert UserRole to Role enum
        Role role = Role.valueOf(user.role().name());

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(
                user.userId(),
                user.email(),
                role
        );

        return new PostRefreshResponse(
                accessToken,
                jwtProperties.getAccessTokenExpiration() / 1000 // Convert to seconds
        );
    }
}
