package com.isipathana.meditationcenter.rest.blog.getTags;

import java.util.List;
import java.util.stream.Stream;

/**
 * Response builder interface for GET /api/blog/tags endpoint.
 * Transforms tag domain objects to API response DTOs.
 *
 * @author Sathira Basnayake
 */
public interface GetPublicBlogTagsResponseBuilder {

    /**
     * Builds list response from blog tags.
     *
     * @param tags Stream of tags with published post counts
     * @return List of response DTOs
     */
    List<GetPublicBlogTagsResponse> build(Stream<PublicBlogTagWithCount> tags);
}
