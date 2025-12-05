package com.isipathana.meditationcenter.rest.event.get;

import com.isipathana.meditationcenter.cache.EventImageUrlsCache;
import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for GetEventsHttpRepository.
 * Tests presigned URL generation for event images with R2 storage.
 * <p>
 * Pattern: Mock R2 clients (no real network calls), test caching logic.
 * Reference: bow-products GetUserFavoriteHttpRepositoryTest
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class GetEventsHttpRepositoryTest {

    private static final String BUCKET_NAME = "meditation-center-events";
    private static final Duration PRESIGNED_URL_TIMEOUT = Duration.ofMinutes(5);
    private static final R2ClientProperties.Bucket BUCKET = new R2ClientProperties.Bucket(BUCKET_NAME, PRESIGNED_URL_TIMEOUT);

    private static final String IMAGE_KEY = "events/1/cover/abc123.jpg";
    private static final String PRESIGNED_URL = "https://r2.example.com/signed-url?token=xyz";

    @Mock
    private R2PresignerClient r2PresignerClient;

    @Mock
    private EventImageUrlsCache cache;

    @Mock
    private R2ClientProperties r2ClientProperties;

    @InjectMocks
    private GetEventsHttpRepository httpRepository;

    @BeforeEach
    void setUp() {
        // Mock R2ClientProperties to return bucket configuration
        when(r2ClientProperties.buckets()).thenReturn(Map.of("events", BUCKET));
    }

    @AfterEach
    void tearDown() {
        reset(r2PresignerClient, cache, r2ClientProperties);
    }

    @Test
    void shouldGeneratePresignedUrl_whenCacheIsMissed() {
        // Arrange
        when(cache.get(IMAGE_KEY)).thenReturn(null);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, IMAGE_KEY, PRESIGNED_URL_TIMEOUT))
                .thenReturn(PRESIGNED_URL);

        // Act
        String actualUrl = httpRepository.generatePresignedUrl(IMAGE_KEY);

        // Assert
        assertEquals(PRESIGNED_URL, actualUrl);
        verify(cache).get(IMAGE_KEY);
        verify(r2PresignerClient).generatePresignedGetUrl(BUCKET_NAME, IMAGE_KEY, PRESIGNED_URL_TIMEOUT);
        verify(cache).put(IMAGE_KEY, PRESIGNED_URL);
    }

    @Test
    void shouldReturnCachedUrl_whenCacheIsHit() {
        // Arrange
        when(cache.get(IMAGE_KEY)).thenReturn(PRESIGNED_URL);

        // Act
        String actualUrl = httpRepository.generatePresignedUrl(IMAGE_KEY);

        // Assert
        assertEquals(PRESIGNED_URL, actualUrl);
        verify(cache).get(IMAGE_KEY);
        verifyNoInteractions(r2PresignerClient); // Should NOT call R2 client when cache hits
        verify(cache, never()).put(anyString(), anyString());
    }

    @Test
    void shouldReturnNull_whenImageKeyIsNull() {
        // Act
        String actualUrl = httpRepository.generatePresignedUrl(null);

        // Assert
        assertNull(actualUrl);
        verifyNoInteractions(cache);
        verifyNoInteractions(r2PresignerClient);
    }

    @Test
    void shouldReturnNull_whenImageKeyIsEmpty() {
        // Act
        String actualUrl = httpRepository.generatePresignedUrl("");

        // Assert
        assertNull(actualUrl);
        verifyNoInteractions(cache);
        verifyNoInteractions(r2PresignerClient);
    }

    @Test
    void shouldReturnNull_whenPresignedUrlGenerationFails() {
        // Arrange
        when(cache.get(IMAGE_KEY)).thenReturn(null);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, IMAGE_KEY, PRESIGNED_URL_TIMEOUT))
                .thenReturn(null); // Simulate failure

        // Act
        String actualUrl = httpRepository.generatePresignedUrl(IMAGE_KEY);

        // Assert
        assertNull(actualUrl);
        verify(cache).get(IMAGE_KEY);
        verify(r2PresignerClient).generatePresignedGetUrl(BUCKET_NAME, IMAGE_KEY, PRESIGNED_URL_TIMEOUT);
        verify(cache, never()).put(anyString(), anyString()); // Should NOT cache null
    }

    @Test
    void shouldCacheGeneratedUrl_afterSuccessfulGeneration() {
        // Arrange
        when(cache.get(IMAGE_KEY)).thenReturn(null);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, IMAGE_KEY, PRESIGNED_URL_TIMEOUT))
                .thenReturn(PRESIGNED_URL);

        // Act
        httpRepository.generatePresignedUrl(IMAGE_KEY);

        // Assert
        verify(cache).put(IMAGE_KEY, PRESIGNED_URL);
    }

    @Test
    void shouldGenerateMultiplePresignedUrls_fromSet() {
        // Arrange
        String key1 = "events/1/cover/img1.jpg";
        String key2 = "events/1/gallery/img2.jpg";
        String key3 = "events/1/gallery/img3.jpg";

        String url1 = "https://r2.example.com/img1?token=1";
        String url2 = "https://r2.example.com/img2?token=2";
        String url3 = "https://r2.example.com/img3?token=3";

        Set<String> imageKeys = Set.of(key1, key2, key3);

        // Mock cache misses for all keys
        when(cache.get(key1)).thenReturn(null);
        when(cache.get(key2)).thenReturn(null);
        when(cache.get(key3)).thenReturn(null);

        // Mock presigned URL generation
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key1, PRESIGNED_URL_TIMEOUT)).thenReturn(url1);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key2, PRESIGNED_URL_TIMEOUT)).thenReturn(url2);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key3, PRESIGNED_URL_TIMEOUT)).thenReturn(url3);

        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls(imageKeys);

        // Assert
        assertEquals(3, urlMap.size());
        assertEquals(url1, urlMap.get(key1));
        assertEquals(url2, urlMap.get(key2));
        assertEquals(url3, urlMap.get(key3));

        verify(cache, times(3)).get(anyString());
        verify(r2PresignerClient, times(3)).generatePresignedGetUrl(eq(BUCKET_NAME), anyString(), eq(PRESIGNED_URL_TIMEOUT));
    }

    @Test
    void shouldGenerateMultiplePresignedUrls_fromList() {
        // Arrange
        String key1 = "events/1/cover/img1.jpg";
        String key2 = "events/1/gallery/img2.jpg";

        String url1 = "https://r2.example.com/img1?token=1";
        String url2 = "https://r2.example.com/img2?token=2";

        List<String> imageKeys = List.of(key1, key2);

        when(cache.get(key1)).thenReturn(null);
        when(cache.get(key2)).thenReturn(null);

        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key1, PRESIGNED_URL_TIMEOUT)).thenReturn(url1);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key2, PRESIGNED_URL_TIMEOUT)).thenReturn(url2);

        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls(imageKeys);

        // Assert
        assertEquals(2, urlMap.size());
        assertEquals(url1, urlMap.get(key1));
        assertEquals(url2, urlMap.get(key2));
    }

    @Test
    void shouldReturnEmptyMap_whenImageKeysSetIsNull() {
        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls((Set<String>) null);

        // Assert
        assertNotNull(urlMap);
        assertTrue(urlMap.isEmpty());
        verifyNoInteractions(cache);
        verifyNoInteractions(r2PresignerClient);
    }

    @Test
    void shouldReturnEmptyMap_whenImageKeysSetIsEmpty() {
        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls(Set.of());

        // Assert
        assertNotNull(urlMap);
        assertTrue(urlMap.isEmpty());
        verifyNoInteractions(cache);
        verifyNoInteractions(r2PresignerClient);
    }

    @Test
    void shouldReturnEmptyMap_whenImageKeysListIsNull() {
        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls((List<String>) null);

        // Assert
        assertNotNull(urlMap);
        assertTrue(urlMap.isEmpty());
    }

    @Test
    void shouldReturnEmptyMap_whenImageKeysListIsEmpty() {
        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls(List.of());

        // Assert
        assertNotNull(urlMap);
        assertTrue(urlMap.isEmpty());
    }

//    @Test
//    void shouldSkipNullAndEmptyKeys_inMultipleUrlGeneration() {
//        // Arrange
//        String validKey = "events/1/cover/img1.jpg";
//        String validUrl = "https://r2.example.com/img1?token=1";
//
//        List<String> imageKeys = List.of(validKey, null, "", "  ");
//
//        when(cache.get(validKey)).thenReturn(null);
//        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, validKey, PRESIGNED_URL_TIMEOUT))
//                .thenReturn(validUrl);
//
//        // Act
//        Map<String, String> urlMap = httpRepository.generatePresignedUrls(imageKeys);
//
//        // Assert
//        assertEquals(1, urlMap.size());
//        assertEquals(validUrl, urlMap.get(validKey));
//
//        // Should only call for valid key
//        verify(cache, times(1)).get(validKey);
//        verify(r2PresignerClient, times(1)).generatePresignedGetUrl(BUCKET_NAME, validKey, PRESIGNED_URL_TIMEOUT);
//    }

    @Test
    void shouldUseCachedUrls_inMultipleUrlGeneration() {
        // Arrange
        String key1 = "events/1/cover/img1.jpg";
        String key2 = "events/1/gallery/img2.jpg";

        String url1 = "https://r2.example.com/img1?token=1";
        String url2 = "https://r2.example.com/img2?token=2";

        Set<String> imageKeys = Set.of(key1, key2);

        // key1 is cached, key2 is not
        when(cache.get(key1)).thenReturn(url1);
        when(cache.get(key2)).thenReturn(null);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key2, PRESIGNED_URL_TIMEOUT)).thenReturn(url2);

        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls(imageKeys);

        // Assert
        assertEquals(2, urlMap.size());
        assertEquals(url1, urlMap.get(key1));
        assertEquals(url2, urlMap.get(key2));

        // Should only call R2 client for key2 (key1 was cached)
        verify(r2PresignerClient, times(1)).generatePresignedGetUrl(BUCKET_NAME, key2, PRESIGNED_URL_TIMEOUT);
        verify(r2PresignerClient, never()).generatePresignedGetUrl(eq(BUCKET_NAME), eq(key1), any());
    }

    @Test
    void shouldHandlePartialFailures_inMultipleUrlGeneration() {
        // Arrange
        String key1 = "events/1/cover/img1.jpg";
        String key2 = "events/1/gallery/img2.jpg";
        String key3 = "events/1/gallery/img3.jpg";

        String url1 = "https://r2.example.com/img1?token=1";
        String url3 = "https://r2.example.com/img3?token=3";

        Set<String> imageKeys = Set.of(key1, key2, key3);

        when(cache.get(any())).thenReturn(null);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key1, PRESIGNED_URL_TIMEOUT)).thenReturn(url1);
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key2, PRESIGNED_URL_TIMEOUT)).thenReturn(null); // Failure
        when(r2PresignerClient.generatePresignedGetUrl(BUCKET_NAME, key3, PRESIGNED_URL_TIMEOUT)).thenReturn(url3);

        // Act
        Map<String, String> urlMap = httpRepository.generatePresignedUrls(imageKeys);

        // Assert
        assertEquals(2, urlMap.size()); // Only successful URLs
        assertEquals(url1, urlMap.get(key1));
        assertNull(urlMap.get(key2)); // Failed key not in map
        assertEquals(url3, urlMap.get(key3));
    }
}
