package com.isipathana.meditationcenter.rest.blog.get;

import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * Repository implementation for generating presigned URLs for blog post images.
 * <p>
 * Only active when r2.enabled=true in application.properties.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class GetBlogPostsHttpRepository implements GetBlogPostsHttpDataAccess {

    private final R2PresignerClient r2PresignerClient;
    private final R2ClientProperties r2ClientProperties;

    private static final Duration URL_EXPIRY = Duration.ofMinutes(5);

    @Override
    public String generatePresignedUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        String bucketName = r2ClientProperties.buckets().get("blog-posts").bucketName();
        return r2PresignerClient.generatePresignedGetUrl(bucketName, key, URL_EXPIRY);
    }
}
