package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Repository implementation for generating presigned URLs for program images (PATCH).
 * Only active when r2.enabled=true in application.properties.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class PatchProgramHttpRepository implements PatchProgramHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(PatchProgramHttpRepository.class);

    private final R2PresignerClient r2PresignerClient;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public String generatePresignedUrl(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            logger.debug("No image key provided, returning null");
            return null;
        }

        String bucketName = r2ClientProperties.buckets().get("programs").bucketName();
        Duration expiry = r2ClientProperties.buckets().get("programs").presignedUrlExpiry();

        try {
            String presignedUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, imageKey, expiry);
            logger.debug("Generated presigned URL for image key: {}", imageKey);
            return presignedUrl;
        } catch (Exception e) {
            logger.error("Failed to generate presigned URL for image key: {}", imageKey, e);
            return null;
        }
    }

    @Override
    public Map<String, String> generatePresignedUrls(Set<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            logger.debug("No image keys provided, returning empty map");
            return Map.of();
        }

        Map<String, String> urlMap = new HashMap<>();
        String bucketName = r2ClientProperties.buckets().get("programs").bucketName();
        Duration expiry = r2ClientProperties.buckets().get("programs").presignedUrlExpiry();

        for (String imageKey : imageKeys) {
            try {
                String presignedUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, imageKey, expiry);
                urlMap.put(imageKey, presignedUrl);
                logger.debug("Generated presigned URL for image key: {}", imageKey);
            } catch (Exception e) {
                logger.error("Failed to generate presigned URL for image key: {}", imageKey, e);
            }
        }

        logger.info("Generated {} presigned URLs for program images", urlMap.size());
        return urlMap;
    }
}
