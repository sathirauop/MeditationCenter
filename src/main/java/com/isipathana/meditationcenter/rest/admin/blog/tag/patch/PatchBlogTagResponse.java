package com.isipathana.meditationcenter.rest.admin.blog.tag.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

/**
 * Response for blog tag update.
 *
 * @author Sathira Basnayake
 */
@Builder
public record PatchBlogTagResponse(
        @JsonProperty("tag_id")
        Long tagId,

        String name,

        @JsonProperty("name_si")
        String nameSi,

        String slug
) implements ApiResponse {
}
