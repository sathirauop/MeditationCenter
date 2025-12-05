package com.isipathana.meditationcenter.rest.auth.refresh;

import com.isipathana.meditationcenter.records.user.User;

/**
 * Data access interface for token refresh.
 */
public interface PostRefreshDataAccess {

    /**
     * Find user by ID.
     *
     * @param userId User ID to search for
     * @return User if found, null otherwise
     */
    User findById(Long userId);
}
