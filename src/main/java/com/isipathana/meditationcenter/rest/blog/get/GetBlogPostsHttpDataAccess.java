package com.isipathana.meditationcenter.rest.blog.get;

/**
 * HTTP data access interface for blog post R2 operations.
 * Handles presigned URL generation for blog post images.
 *
 * @author Sathira Basnayake
 */
public interface GetBlogPostsHttpDataAccess {

    /**
     * Generates a presigned URL for a blog post cover image.
     *
     * @param key R2 object key
     * @return Presigned URL with 5-minute expiry
     */
    String generatePresignedUrl(String key);
}
