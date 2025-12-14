package com.isipathana.meditationcenter.rest.admin.user.post;

import com.isipathana.meditationcenter.records.user.User;

/**
 * Response builder interface for created user.
 *
 * @author Sathira Basnayake
 */
public interface PostUserResponseBuilder {

    /**
     * Build response from created user.
     *
     * @param user Created user domain object
     * @return User response DTO
     */
    PostUserResponse build(User user);
}
