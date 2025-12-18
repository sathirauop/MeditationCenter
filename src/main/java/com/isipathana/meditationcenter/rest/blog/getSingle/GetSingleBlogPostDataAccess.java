package com.isipathana.meditationcenter.rest.blog.getSingle;

import com.isipathana.meditationcenter.records.blog.BlogPostWithTags;

import java.util.Optional;

/**
 * Data access interface for fetching a single published blog post by slug.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleBlogPostDataAccess {

    /**
     * Fetches a published blog post by slug.
     *
     * @param slug The post slug
     * @return Optional containing the post if found and published, empty otherwise
     */
    Optional<BlogPostWithTags> getPublishedBlogPostBySlug(String slug);

    /**
     * Increments the view count for a blog post.
     *
     * @param postId The post ID
     */
    void incrementViewCount(Long postId);
}
