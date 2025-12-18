package com.isipathana.meditationcenter.rest.blog.getSingle;

import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
public class GetSingleBlogPostHttpRepository implements GetSingleBlogPostHttpDataAccess {

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

    @Override
    public Map<String, String> generatePresignedUrls(Set<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Map.of();
        }

        String bucketName = r2ClientProperties.buckets().get("blog-posts").bucketName();
        Map<String, String> urls = new HashMap<>();

        for (String key : keys) {
            if (key != null && !key.isBlank()) {
                String url = r2PresignerClient.generatePresignedGetUrl(bucketName, key, URL_EXPIRY);
                if (url != null) {
                    urls.put(key, url);
                }
            }
        }

        return urls;
    }
}
