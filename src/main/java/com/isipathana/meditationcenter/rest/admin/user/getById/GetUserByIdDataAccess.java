package com.isipathana.meditationcenter.rest.admin.user.getById;

import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.records.user.UserStatistics;

import java.util.Optional;

/**
 * Data access interface for getting user by ID with statistics.
 *
 * @author Sathira Basnayake
 */
public interface GetUserByIdDataAccess {

    /**
     * Find user by ID.
     *
     * @param userId User ID
     * @return Optional containing user if found
     */
    Optional<User> findById(Long userId);

    /**
     * Get user statistics (bookings, donations, event registrations).
     *
     * @param userId User ID
     * @return User statistics
     */
    UserStatistics getUserStatistics(Long userId);
}
