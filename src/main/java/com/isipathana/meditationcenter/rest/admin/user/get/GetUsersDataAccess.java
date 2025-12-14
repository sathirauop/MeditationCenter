package com.isipathana.meditationcenter.rest.admin.user.get;

import com.isipathana.meditationcenter.records.user.UserRole;
import com.isipathana.meditationcenter.records.user.User;

import java.util.stream.Stream;

/**
 * Data access interface for getting users with filtering.
 *
 * @author Sathira Basnayake
 */
public interface GetUsersDataAccess {

    /**
     * Find users with pagination and optional filtering.
     *
     * @param limit Maximum number of results
     * @param offset Pagination offset
     * @param role Optional role filter
     * @param isActive Optional active status filter
     * @param search Optional search term (searches name and email)
     * @return Stream of users matching criteria
     */
    Stream<User> findUsers(Integer limit, Integer offset, UserRole role, Boolean isActive, String search);

    /**
     * Count total users matching the filter criteria.
     *
     * @param role Optional role filter
     * @param isActive Optional active status filter
     * @param search Optional search term
     * @return Total count of matching users
     */
    Long countUsers(UserRole role, Boolean isActive, String search);
}
