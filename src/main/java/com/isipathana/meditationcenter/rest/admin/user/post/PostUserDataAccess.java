package com.isipathana.meditationcenter.rest.admin.user.post;

import com.isipathana.meditationcenter.records.user.User;

/**
 * Data access interface for creating users.
 *
 * @author Sathira Basnayake
 */
public interface PostUserDataAccess {

    /**
     * Check if user exists by email.
     *
     * @param email Email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Create a new user.
     *
     * @param user User to create
     * @return Created user with generated ID
     */
    User createUser(User user);
}
