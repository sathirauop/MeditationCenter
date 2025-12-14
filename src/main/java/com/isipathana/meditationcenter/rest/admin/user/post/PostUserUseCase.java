package com.isipathana.meditationcenter.rest.admin.user.post;

import com.isipathana.meditationcenter.exception.ConflictException;
import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for creating a new user (admin endpoint).
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostUserUseCase {

    private final PostUserDataAccess repository;
    private final PostUserResponseBuilder presenter;
    private final PasswordEncoder passwordEncoder;

    /**
     * Execute use case to create a new user.
     *
     * @param request User creation request
     * @return Created user response
     * @throws ConflictException if email already exists
     */
    @Transactional
    public PostUserResponse execute(PostUserRequest request) {
        log.info("Creating new user with email: {}", request.email());

        // Check if email already exists
        if (repository.existsByEmail(request.email())) {
            log.warn("User with email {} already exists", request.email());
            throw new ConflictException("User with email " + request.email() + " already exists");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.password());

        // Build user domain object
        User userToCreate = User.builder()
                .email(request.email())
                .password(hashedPassword)
                .name(request.name())
                .mobileNumber(request.mobileNumber())
                .role(request.role())
                .isActive(request.isActive() != null ? request.isActive() : true)
                .emailVerified(request.emailVerified() != null ? request.emailVerified() : false)
                .build();

        // Create user
        User createdUser = repository.createUser(userToCreate);

        log.info("User created successfully with ID: {}", createdUser.userId());

        // Build response
        return presenter.build(createdUser);
    }
}
