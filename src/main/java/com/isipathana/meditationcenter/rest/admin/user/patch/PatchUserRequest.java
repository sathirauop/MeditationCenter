package com.isipathana.meditationcenter.rest.admin.user.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user information.
 * All fields are optional - only provided fields will be updated.
 *
 * @author Sathira Basnayake
 */
public record PatchUserRequest(
        @Email(message = "Email must be valid")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        String name,

        @JsonProperty("mobile_number")
        String mobileNumber
) {}
