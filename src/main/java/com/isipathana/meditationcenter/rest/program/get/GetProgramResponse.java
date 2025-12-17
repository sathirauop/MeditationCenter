package com.isipathana.meditationcenter.rest.program.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Set;

/**
 * Response DTO for getting a meditation program (public).
 * Returns presigned image URLs, not keys.
 * Excludes isActive and createdAt fields (cleaner public response).
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetProgramResponse(
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
        Set<String> galleryImageUrls
) {}
