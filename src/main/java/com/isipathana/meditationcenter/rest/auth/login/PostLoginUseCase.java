package com.isipathana.meditationcenter.rest.auth.login;

import com.isipathana.meditationcenter.config.JwtProperties;
import com.isipathana.meditationcenter.exception.AuthenticationException;
import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.security.Role;
import com.isipathana.meditationcenter.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for user login.
 * Handles business logic for authenticating users and generating tokens.
 */
@Service
@RequiredArgsConstructor
public class PostLoginUseCase {

    private final PostLoginDataAccess repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /**
     * Execute user login.
     *
     * @param request Login request
     * @return Response with access and refresh tokens
     * @throws AuthenticationException if credentials are invalid or account is inactive
     */
    @Transactional(readOnly = true)
    public PostLoginResponse execute(PostLoginRequest request) {
        // Find user by email (with password)
        User user = repository.findByEmailWithPassword(request.email());

        if (user == null) {
            throw new AuthenticationException("Invalid email or password");
        }

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.password())) {
            throw new AuthenticationException("Invalid email or password");
        }

        // Check if account is active
        if (user.isActive() == null || !user.isActive()) {
            throw new AuthenticationException("Account is inactive");
        }

        // Convert UserRole to Role enum
        Role role = Role.valueOf(user.role().name());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(
                user.userId(),
                user.email(),
                role
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.userId(),
                user.email()
        );

        return new PostLoginResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpiration() / 1000 // Convert to seconds
        );
    }
}
