package com.isipathana.meditationcenter.rest.blog.getSingle;

import java.util.Map;
import java.util.Set;

/**
 * HTTP data access interface for single blog post R2 operations.
 * Handles presigned URL generation for blog post images.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleBlogPostHttpDataAccess {

    /**
     * Generates a presigned URL for a single image key.
     *
     * @param key R2 object key
     * @return Presigned URL with 5-minute expiry
     */
    String generatePresignedUrl(String key);

    /**
     * Generates presigned URLs for multiple image keys.
     *
     * @param keys Set of R2 object keys
     * @return Map of key → presigned URL
     */
    Map<String, String> generatePresignedUrls(Set<String> keys);
}
