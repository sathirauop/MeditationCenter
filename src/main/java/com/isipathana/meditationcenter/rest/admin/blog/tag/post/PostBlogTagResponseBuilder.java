package com.isipathana.meditationcenter.rest.admin.blog.tag.post;

import com.isipathana.meditationcenter.records.blog.BlogTag;

/**
 * Response builder interface for blog tag creation.
 *
 * @author Sathira Basnayake
 */
public interface PostBlogTagResponseBuilder {

    /**
     * Builds a response from a created blog tag.
     *
     * @param tag The created blog tag
     * @return The response DTO
     */
    PostBlogTagResponse build(BlogTag tag);
}
