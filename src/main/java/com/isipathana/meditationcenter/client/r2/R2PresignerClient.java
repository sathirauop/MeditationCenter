package com.isipathana.meditationcenter.client.r2;

import org.slf4j.Logger;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

/**
 * Client for generating presigned URLs for Cloudflare R2 (S3-compatible) storage.
 * <p>
 * Presigned URLs provide temporary access to private R2 objects without requiring authentication.
 * URLs expire after a configured duration (typically 5 minutes).
 *
 * @author Claude Code
 */
public class R2PresignerClient {

    private final S3Presigner s3Presigner;
    private final Logger logger;

    public R2PresignerClient(S3Presigner s3Presigner, Logger logger) {
        this.s3Presigner = s3Presigner;
        this.logger = logger;
    }

    /**
     * Generates a presigned GET URL for an object in R2.
     * <p>
     * The URL allows temporary read access to the object without authentication.
     *
     * @param bucketName The name of the R2 bucket
     * @param key        The object key (path) in R2
     * @param expiry     Duration for which the URL is valid
     * @return The presigned URL as a string, or null if generation failed
     */
    public String generatePresignedGetUrl(String bucketName, String key, Duration expiry) {
        try {
            logger.debug("Generating presigned URL: bucket={}, key={}, expiry={}", bucketName, key, expiry);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiry)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toString();

            logger.debug("Generated presigned URL: bucket={}, key={}, url={}", bucketName, key, url);
            return url;

        } catch (Exception e) {
            logger.error("Failed to generate presigned URL: bucket={}, key={}, error={}", bucketName, key, e.getMessage(), e);
            return null;
        }
    }
}
