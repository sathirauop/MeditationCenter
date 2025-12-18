package com.isipathana.meditationcenter.rest.admin.blog.post.getSingle;

import com.isipathana.meditationcenter.records.blog.BlogPost;

import java.util.Optional;

/**
 * Data access interface for fetching a single blog post for admin editing.
 *
 * @author Sathira Basnayake
 */
public interface GetAdminBlogPostDataAccess {

    /**
     * Fetches a blog post by ID (includes drafts, no status filter).
     *
     * @param postId The post ID
     * @return Optional containing the post if found and not deleted
     */
    Optional<BlogPost> getBlogPostById(Long postId);
}
