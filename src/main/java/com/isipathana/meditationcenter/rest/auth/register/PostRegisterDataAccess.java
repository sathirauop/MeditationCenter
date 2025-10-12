package com.isipathana.meditationcenter.rest.auth.register;

import com.isipathana.meditationcenter.records.user.User;

/**
 * Data access interface for user registration.
 */
public interface PostRegisterDataAccess {

    /**
     * Check if a user with the given email already exists.
     *
     * @param email Email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Create a new user.
     *
     * @param user User to create
     * @return Created user
     */
    User createUser(User user);
}
