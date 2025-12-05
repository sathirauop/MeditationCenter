package com.isipathana.meditationcenter.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * Configuration properties for Cloudflare R2 (S3-compatible) object storage.
 * <p>
 * Loaded from application.properties with prefix "r2".
 * <p>
 * Example configuration:
 * <pre>
 * r2.enabled=true
 * r2.account-id=your-account-id
 * r2.access-key-id=your-access-key
 * r2.secret-access-key=your-secret
 * r2.endpoint=https://account-id.r2.cloudflarestorage.com
 * r2.buckets.meditation-images.bucket-name=meditation-center-images
 * r2.buckets.meditation-images.presigned-url-expiry=5m
 * </pre>
 */
@ConfigurationProperties(prefix = "r2")
public record R2ClientProperties(
        String accountId,
        String accessKeyId,
        String secretAccessKey,
        String endpoint,
        String publicUrl,
        Map<String, Bucket> buckets
) {
    /**
     * Configuration for a single R2 bucket.
     *
     * @param bucketName        The name of the R2 bucket
     * @param presignedUrlExpiry Duration for which presigned URLs are valid
     */
    public record Bucket(
            String bucketName,
            Duration presignedUrlExpiry
    ) {}
}
