package com.isipathana.meditationcenter.rest.admin.blog.post.post;

import com.isipathana.meditationcenter.records.blog.BlogPost;

import java.util.Set;

/**
 * Data access interface for creating blog posts.
 *
 * @author Sathira Basnayake
 */
public interface PostBlogPostDataAccess {

    /**
     * Creates a new blog post in the database.
     *
     * @param post The blog post to create
     * @return The created blog post with generated ID and timestamps
     */
    BlogPost createBlogPost(BlogPost post);

    /**
     * Updates blog post image keys after R2 upload.
     *
     * @param postId          The post ID
     * @param coverImageKey   The cover image key (can be null)
     * @param galleryImageKeys The gallery image keys (can be null/empty)
     * @return The updated blog post
     */
    BlogPost updateImageKeys(Long postId, String coverImageKey, Set<String> galleryImageKeys);

    /**
     * Associates tags with a blog post.
     *
     * @param postId The post ID
     * @param tagIds The tag IDs to associate
     */
    void associateTags(Long postId, Set<Long> tagIds);

    /**
     * Checks if a slug already exists.
     *
     * @param slug The slug to check
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);
}
