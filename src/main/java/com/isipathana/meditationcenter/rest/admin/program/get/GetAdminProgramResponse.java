package com.isipathana.meditationcenter.rest.admin.program.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for getting a meditation program (admin).
 * Returns presigned image URLs, not keys.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetAdminProgramResponse(
        @JsonProperty("meditation_program_id")
        Long meditationProgramId,

        @JsonProperty("name")
        String name,

        @JsonProperty("description")
        String description,

        @JsonProperty("name_si")
        String nameSi,

        @JsonProperty("description_si")
        String descriptionSi,

        @JsonProperty("max_seats")
        Integer maxSeats,

        @JsonProperty("cover_image_url")
        String coverImageUrl,

        @JsonProperty("gallery_image_urls")
        Set<String> galleryImageUrls,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {}
