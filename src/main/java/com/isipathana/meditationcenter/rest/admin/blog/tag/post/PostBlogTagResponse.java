package com.isipathana.meditationcenter.rest.admin.blog.tag.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response for blog tag creation.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PostBlogTagResponse(
        @JsonProperty("tag_id")
        Long tagId,

        String name,

        @JsonProperty("name_si")
        String nameSi,

        String slug,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) implements ApiResponse {
}
