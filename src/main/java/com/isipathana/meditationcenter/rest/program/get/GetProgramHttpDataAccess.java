package com.isipathana.meditationcenter.rest.program.get;

import java.util.Map;
import java.util.Set;

/**
 * HTTP data access interface for generating presigned URLs for program images (public).
 *
 * @author Sathira Basnayake
 */
public interface GetProgramHttpDataAccess {

    /**
     * Generates a presigned URL for a single image key.
     *
     * @param imageKey The R2 image key
     * @return The presigned URL
     */
    String generatePresignedUrl(String imageKey);

    /**
     * Generates presigned URLs for multiple image keys.
     *
     * @param imageKeys The R2 image keys
     * @return Map of image key to presigned URL
     */
    Map<String, String> generatePresignedUrls(Set<String> imageKeys);
}
