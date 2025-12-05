package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import com.isipathana.meditationcenter.cache.EventImageUrlsCache;
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
 * Repository implementation for generating presigned URLs for event images from R2 storage.
 * <p>
 * Uses Caffeine cache with 4-minute TTL to avoid regenerating presigned URLs on every request.
 * Presigned URLs expire after 5 minutes, providing a 1-minute safety margin.
 * <p>
 * Only active when r2.enabled=true in application.properties.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class GetSingleEventHttpRepository implements GetSingleEventHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(GetSingleEventHttpRepository.class);

    private final R2PresignerClient r2PresignerClient;
    private final EventImageUrlsCache cache;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public String generatePresignedUrl(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return null;
        }

        // Check cache first
        String cachedUrl = cache.get(imageKey);
        if (cachedUrl != null) {
            logger.debug("Cache hit for image key: {}", imageKey);
            return cachedUrl;
        }

        // Generate new presigned URL
        logger.debug("Cache miss for image key: {}, generating new presigned URL", imageKey);

        String bucketName = r2ClientProperties.buckets().get("events").bucketName();
        Duration expiry = r2ClientProperties.buckets().get("events").presignedUrlExpiry();

        String presignedUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, imageKey, expiry);

        if (presignedUrl != null) {
            // Cache the URL
            cache.put(imageKey, presignedUrl);
            logger.debug("Generated and cached presigned URL for key: {}", imageKey);
        } else {
            logger.error("Failed to generate presigned URL for key: {}", imageKey);
        }

        return presignedUrl;
    }

    @Override
    public Map<String, String> generatePresignedUrls(Set<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            return Map.of();
        }

        Map<String, String> urlMap = new HashMap<>();

        for (String imageKey : imageKeys) {
            if (imageKey != null && !imageKey.isEmpty()) {
                String presignedUrl = generatePresignedUrl(imageKey);
                if (presignedUrl != null) {
                    urlMap.put(imageKey, presignedUrl);
                }
            }
        }

        logger.debug("Generated {} presigned URLs out of {} image keys", urlMap.size(), imageKeys.size());
        return urlMap;
    }
}
