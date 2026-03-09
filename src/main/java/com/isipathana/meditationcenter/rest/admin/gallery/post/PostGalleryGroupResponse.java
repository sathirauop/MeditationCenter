package com.isipathana.meditationcenter.rest.admin.gallery.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response DTO for creating a gallery group.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PostGalleryGroupResponse(
        @JsonProperty("group_id") Long groupId,

        String name,

        @JsonProperty("name_si") String nameSi,

        @JsonProperty("sort_order") Integer sortOrder,

        Boolean active,

        @JsonProperty("created_at") LocalDateTime createdAt) {
}
