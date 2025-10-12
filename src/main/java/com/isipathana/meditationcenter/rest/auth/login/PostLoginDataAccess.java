package com.isipathana.meditationcenter.rest.auth.login;

import com.isipathana.meditationcenter.records.user.User;

/**
 * Data access interface for user login.
 */
public interface PostLoginDataAccess {

    /**
     * Find user by email with password field included.
     *
     * @param email Email to search for
     * @return User if found, null otherwise
     */
    User findByEmailWithPassword(String email);
}
