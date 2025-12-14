package com.isipathana.meditationcenter.rest.admin.user.patch;

import com.isipathana.meditationcenter.records.user.User;

/**
 * Response builder interface for updated user.
 *
 * @author Sathira Basnayake
 */
public interface PatchUserResponseBuilder {

    /**
     * Build response from updated user.
     *
     * @param user Updated user domain object
     * @return User response DTO
     */
    PatchUserResponse build(User user);
}
