package com.isipathana.meditationcenter.rest.admin.user.patch;

import com.isipathana.meditationcenter.records.user.User;

import java.util.Optional;

/**
 * Data access interface for updating users.
 *
 * @author Sathira Basnayake
 */
public interface PatchUserDataAccess {

    /**
     * Find user by ID.
     *
     * @param userId User ID
     * @return User if found, empty otherwise
     */
    Optional<User> findById(Long userId);

    /**
     * Check if email exists for a different user.
     *
     * @param email  Email to check
     * @param userId Current user ID to exclude
     * @return true if email exists for another user, false otherwise
     */
    boolean existsByEmailAndNotUserId(String email, Long userId);

    /**
     * Update user information.
     * Only non-null fields in the user object will be updated.
     *
     * @param userId User ID
     * @param user   User with fields to update
     * @return Updated user
     */
    User updateUser(Long userId, User user);
}
