package com.isipathana.meditationcenter.rest.admin.user.patch;

import com.isipathana.meditationcenter.exception.ConflictException;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating user information.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchUserUseCase {

    private final PatchUserDataAccess repository;
    private final PatchUserResponseBuilder presenter;
    private final PasswordEncoder passwordEncoder;

    /**
     * Execute use case to update user information.
     *
     * @param userId  User ID
     * @param request Update request
     * @return Updated user response
     * @throws ResourceNotFoundException  if user not found
     * @throws ConflictException if email already exists for another user
     */
    @Transactional
    public PatchUserResponse execute(Long userId, PatchUserRequest request) {
        log.info("Updating user with ID: {}", userId);

        // Verify user exists
        User existingUser = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if email is being changed and already exists
        if (request.email() != null && !request.email().equals(existingUser.email())) {
            if (repository.existsByEmailAndNotUserId(request.email(), userId)) {
                log.warn("Email {} already exists for another user", request.email());
                throw new ConflictException("Email " + request.email() + " already exists");
            }
        }

        // Build user update object with only non-null fields
        User.UserBuilder updateBuilder = User.builder();

        if (request.email() != null) {
            updateBuilder.email(request.email());
        }

        if (request.password() != null) {
            String hashedPassword = passwordEncoder.encode(request.password());
            updateBuilder.password(hashedPassword);
        }

        if (request.name() != null) {
            updateBuilder.name(request.name());
        }

        if (request.mobileNumber() != null) {
            updateBuilder.mobileNumber(request.mobileNumber());
        }

        User userToUpdate = updateBuilder.build();

        // Update user
        User updatedUser = repository.updateUser(userId, userToUpdate);

        log.info("User {} updated successfully", userId);

        // Build response
        return presenter.build(updatedUser);
    }
}
