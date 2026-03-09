package com.isipathana.meditationcenter.rest.gallery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

/**
 * Response DTO for public gallery endpoint.
 * Contains gallery groups with their photos.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetPublicGalleryResponse(
                @JsonProperty("group_id") Long groupId,

                String name,

                @JsonProperty("name_si") String nameSi,

                List<PhotoResponse> photos) {
        @Builder
        public record PhotoResponse(
                        @JsonProperty("photo_id") Long photoId,

                        @JsonProperty("image_key") String imageKey,

                        String caption,

                        @JsonProperty("caption_si") String captionSi) {
        }
}
