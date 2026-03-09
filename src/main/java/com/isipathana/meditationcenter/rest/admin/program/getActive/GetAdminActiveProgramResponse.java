package com.isipathana.meditationcenter.rest.admin.program.getActive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Response DTO for getting the active meditation program (admin).
 * Returns presigned image URLs, not keys.
 * Includes all fields including isActive and timestamps.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetAdminActiveProgramResponse(
                @JsonProperty("meditation_program_id") Long meditationProgramId,

                @JsonProperty("name") String name,

                @JsonProperty("description") String description,

                @JsonProperty("name_si") String nameSi,

                @JsonProperty("description_si") String descriptionSi,

                @JsonProperty("max_seats") Integer maxSeats,

                @JsonProperty("cover_image_url") String coverImageUrl,

                @JsonProperty("gallery_image_urls") Set<String> galleryImageUrls,

                /**
                 * Gallery images with both R2 key and presigned URL.
                 * Used by admin UI to identify images for removal.
                 */
                @JsonProperty("gallery_images") List<GalleryImage> galleryImages,

                @JsonProperty("is_active") Boolean isActive,

                @JsonProperty("created_at") LocalDateTime createdAt,

                @JsonProperty("updated_at") LocalDateTime updatedAt) {

        /**
         * Represents a gallery image with its R2 key and presigned URL.
         */
        @Builder
        public record GalleryImage(
                        @JsonProperty("key") String key,
                        @JsonProperty("url") String url) {
        }
}
