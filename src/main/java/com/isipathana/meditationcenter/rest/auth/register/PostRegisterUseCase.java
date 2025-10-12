package com.isipathana.meditationcenter.rest.auth.register;

import com.isipathana.meditationcenter.config.JwtProperties;
import com.isipathana.meditationcenter.exception.ConflictException;
import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.records.user.UserRole;
import com.isipathana.meditationcenter.security.Role;
import com.isipathana.meditationcenter.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for user registration.
 * Handles business logic for creating new users and generating authentication tokens.
 */
@Service
@RequiredArgsConstructor
public class PostRegisterUseCase {

    private final PostRegisterDataAccess repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /**
     * Execute user registration.
     *
     * @param request Registration request
     * @return Response with access and refresh tokens
     * @throws ConflictException if email already exists
     */
    @Transactional
    public PostRegisterResponse execute(PostRegisterRequest request) {
        // Check if email already exists
        if (repository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.password());

        // Create user
        User user = User.builder()
                .email(request.email())
                .password(hashedPassword)
                .name(request.name())
                .mobileNumber(request.mobileNumber())
                .role(UserRole.USER) // Default role
                .isActive(true)
                .emailVerified(false)
                .build();

        User savedUser = repository.createUser(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(
                savedUser.userId(),
                savedUser.email(),
                Role.USER
        );

        String refreshToken = jwtService.generateRefreshToken(
                savedUser.userId(),
                savedUser.email()
        );

        return new PostRegisterResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpiration() / 1000 // Convert to seconds
        );
    }
}
