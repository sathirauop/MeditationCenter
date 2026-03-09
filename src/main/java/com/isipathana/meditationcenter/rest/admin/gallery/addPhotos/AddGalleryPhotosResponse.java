package com.isipathana.meditationcenter.rest.admin.gallery.addPhotos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for adding photos to a gallery group.
 *
 * @author Sathira Basnayake
 */
@Builder
public record AddGalleryPhotosResponse(
        @JsonProperty("group_id") Long groupId,

        @JsonProperty("photos_added") Integer photosAdded,

        List<PhotoResponse> photos) {
    @Builder
    public record PhotoResponse(
            @JsonProperty("photo_id") Long photoId,

            @JsonProperty("image_key") String imageKey,

            @JsonProperty("sort_order") Integer sortOrder,

            @JsonProperty("created_at") LocalDateTime createdAt) {
    }
}
