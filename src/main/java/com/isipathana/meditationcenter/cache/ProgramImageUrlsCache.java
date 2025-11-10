package com.isipathana.meditationcenter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Cache for meditation program image presigned URLs.
 * <p>
 * Caches presigned URLs with a 4-minute TTL to avoid regenerating them on every request.
 * Since presigned URLs expire after 5 minutes, the 4-minute cache ensures URLs are always valid
 * with a 1-minute safety margin.
 *
 * @author Claude Code
 */
@Component
public class ProgramImageUrlsCache {

    private final Cache<String, String> cache;

    public ProgramImageUrlsCache() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(4))
                .maximumSize(10_000)
                .build();
    }

    /**
     * Gets a presigned URL from cache.
     *
     * @param imageKey The R2 object key
     * @return The cached presigned URL, or null if not found
     */
    public String get(String imageKey) {
        return cache.getIfPresent(imageKey);
    }

    /**
     * Puts a presigned URL into cache.
     *
     * @param imageKey     The R2 object key
     * @param presignedUrl The presigned URL to cache
     */
    public void put(String imageKey, String presignedUrl) {
        cache.put(imageKey, presignedUrl);
    }

    /**
     * Removes a presigned URL from cache.
     * <p>
     * Useful when an image is deleted or updated.
     *
     * @param imageKey The R2 object key to invalidate
     */
    public void invalidate(String imageKey) {
        cache.invalidate(imageKey);
    }

    /**
     * Clears the entire cache.
     */
    public void clear() {
        cache.invalidateAll();
    }
}
