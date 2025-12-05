package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import java.util.Map;
import java.util.Set;

/**
 * Data access interface for generating presigned URLs for event images from R2 storage.
 * <p>
 * Generates temporary presigned URLs for secure access to event images.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleEventHttpDataAccess {

    /**
     * Generates a presigned URL for a single image key.
     *
     * @param imageKey The R2 object key
     * @return Presigned URL, or null if generation failed
     */
    String generatePresignedUrl(String imageKey);

    /**
     * Generates presigned URLs for multiple image keys.
     *
     * @param imageKeys Set of R2 object keys
     * @return Map of image key to presigned URL
     */
    Map<String, String> generatePresignedUrls(Set<String> imageKeys);
}
