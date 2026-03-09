package com.isipathana.meditationcenter.rest.admin.gallery.getSingle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for GET /api/admin/gallery/{groupId} endpoint.
 * Contains gallery group details with all its photos.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetGalleryGroupResponse(
        @JsonProperty("group_id") Long groupId,

        String name,

        @JsonProperty("name_si") String nameSi,

        @JsonProperty("sort_order") Integer sortOrder,

        Boolean active,

        List<PhotoResponse> photos,

        @JsonProperty("created_at") LocalDateTime createdAt,

        @JsonProperty("updated_at") LocalDateTime updatedAt) {
    @Builder
    public record PhotoResponse(
            @JsonProperty("photo_id") Long photoId,

            @JsonProperty("image_key") String imageKey,

            String caption,

            @JsonProperty("caption_si") String captionSi,

            @JsonProperty("sort_order") Integer sortOrder,

            @JsonProperty("created_at") LocalDateTime createdAt) {
    }
}
