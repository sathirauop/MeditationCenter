package com.isipathana.meditationcenter.rest.admin.blog.tag.get;

import java.util.List;
import java.util.stream.Stream;

/**
 * Response builder interface for blog tags list.
 *
 * @author Sathira Basnayake
 */
public interface GetBlogTagsResponseBuilder {

    /**
     * Builds a list of tag responses from blog tags with counts.
     *
     * @param tagsWithCount Stream of tags with post counts
     * @return List of tag responses
     */
    List<GetBlogTagsResponse> build(Stream<BlogTagWithCount> tagsWithCount);
}
