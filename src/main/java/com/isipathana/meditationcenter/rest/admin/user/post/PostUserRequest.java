package com.isipathana.meditationcenter.rest.admin.user.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.records.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new user (admin endpoint).
 *
 * @author Sathira Basnayake
 */
public record PostUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Name is required")
        String name,

        @JsonProperty("mobile_number")
        String mobileNumber,

        @NotNull(message = "Role is required")
        UserRole role,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("email_verified")
        Boolean emailVerified
) {}
