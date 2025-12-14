package com.isipathana.meditationcenter.rest.admin.user.getById;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import com.isipathana.meditationcenter.records.user.UserStatistics;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response DTO for detailed user information with statistics.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetUserByIdResponse(
        @JsonProperty("user_id") Long userId,
        String email,
        String name,
        @JsonProperty("mobile_number") String mobileNumber,
        String role,
        @JsonProperty("is_active") Boolean isActive,
        @JsonProperty("email_verified") Boolean emailVerified,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt,
        UserStatistics statistics
) implements ApiResponse {}
