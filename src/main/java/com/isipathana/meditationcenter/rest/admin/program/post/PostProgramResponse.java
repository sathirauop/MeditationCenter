package com.isipathana.meditationcenter.rest.admin.program.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for creating a meditation program.
 * Returns image KEYS, not URLs.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PostProgramResponse(
        @JsonProperty("meditation_program_id")
        Long meditationProgramId,

        @JsonProperty("name")
        String name,

        @JsonProperty("description")
        String description,

        @JsonProperty("max_seats")
        Integer maxSeats,

        @JsonProperty("cover_image_key")
        String coverImageKey,

        @JsonProperty("gallery_image_keys")
        Set<String> galleryImageKeys,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {}
