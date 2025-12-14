package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * HTTP repository for generating presigned URLs for book files stored in R2.
 * Only active when R2 is enabled in configuration.
 *
 * @author Sathira Basnayake
 */
@Repository
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
@RequiredArgsConstructor
public class PatchBookHttpRepository implements PatchBookHttpDataAccess {

    private final R2PresignerClient r2PresignerClient;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public String generatePresignedUrl(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            return null;
        }

        // Get books bucket configuration
        String bucketName = r2ClientProperties.buckets().get("books").bucketName();
        Duration expiry = r2ClientProperties.buckets().get("books").presignedUrlExpiry();

        return r2PresignerClient.generatePresignedGetUrl(bucketName, fileKey, expiry);
    }
}
