package com.isipathana.meditationcenter.rest.admin.user.getById;

import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.records.user.UserStatistics;

/**
 * Response builder interface for user details.
 *
 * @author Sathira Basnayake
 */
public interface GetUserByIdResponseBuilder {

    /**
     * Build response from user and statistics.
     *
     * @param user User domain object
     * @param statistics User statistics
     * @return User details response
     */
    GetUserByIdResponse build(User user, UserStatistics statistics);
}
