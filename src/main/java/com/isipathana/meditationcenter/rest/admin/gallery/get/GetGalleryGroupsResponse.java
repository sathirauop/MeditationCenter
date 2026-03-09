package com.isipathana.meditationcenter.rest.admin.gallery.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response DTO for GET /api/admin/gallery endpoint.
 * Contains gallery group summary with photo count.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetGalleryGroupsResponse(
        @JsonProperty("group_id") Long groupId,

        String name,

        @JsonProperty("name_si") String nameSi,

        @JsonProperty("sort_order") Integer sortOrder,

        Boolean active,

        @JsonProperty("photo_count") Integer photoCount,

        @JsonProperty("created_at") LocalDateTime createdAt,

        @JsonProperty("updated_at") LocalDateTime updatedAt) {
}
