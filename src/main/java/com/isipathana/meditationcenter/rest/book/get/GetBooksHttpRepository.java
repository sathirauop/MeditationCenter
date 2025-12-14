package com.isipathana.meditationcenter.rest.book.get;

import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * HTTP repository for generating presigned URLs for book files.
 * Only active when r2.enabled=true.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class GetBooksHttpRepository implements GetBooksHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(GetBooksHttpRepository.class);

    private final R2PresignerClient r2PresignerClient;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public String generatePresignedUrl(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            return null;
        }

        try {
            String bucketName = r2ClientProperties.buckets().get("books").bucketName();
            Duration expiry = r2ClientProperties.buckets().get("books").presignedUrlExpiry();

            String presignedUrl = r2PresignerClient.generatePresignedGetUrl(bucketName, fileKey, expiry);

            logger.debug("Generated presigned URL for file key: {}", fileKey);
            return presignedUrl;

        } catch (Exception e) {
            logger.error("Failed to generate presigned URL for key {}: {}", fileKey, e.getMessage());
            return null;
        }
    }
}
