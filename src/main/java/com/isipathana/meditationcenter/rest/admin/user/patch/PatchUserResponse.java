package com.isipathana.meditationcenter.rest.admin.user.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response DTO for updated user.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PatchUserResponse(
        @JsonProperty("user_id") Long userId,
        String email,
        String name,
        @JsonProperty("mobile_number") String mobileNumber,
        String role,
        @JsonProperty("is_active") Boolean isActive,
        @JsonProperty("email_verified") Boolean emailVerified,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
) implements ApiResponse {}
