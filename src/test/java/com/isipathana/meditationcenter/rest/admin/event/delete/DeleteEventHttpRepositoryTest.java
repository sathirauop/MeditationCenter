package com.isipathana.meditationcenter.rest.admin.event.delete;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for DeleteEventHttpRepository.
 * Tests image deletion operations from R2 storage.
 * <p>
 * Pattern: Mock R2 client, test deletion logic for individual files and folders.
 * Reference: bow-products HttpRepository test patterns
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class DeleteEventHttpRepositoryTest {

    private static final String BUCKET_NAME = "meditation-center-events";
    private static final Duration PRESIGNED_URL_TIMEOUT = Duration.ofMinutes(5);
    private static final R2ClientProperties.Bucket BUCKET = new R2ClientProperties.Bucket(BUCKET_NAME, PRESIGNED_URL_TIMEOUT);

    @Mock
    private R2FileManagerClient r2FileManagerClient;

    @Mock
    private R2ClientProperties r2ClientProperties;

    @InjectMocks
    private DeleteEventHttpRepository httpRepository;

    @BeforeEach
    void setUp() {
        when(r2ClientProperties.buckets()).thenReturn(Map.of("events", BUCKET));
    }

    @AfterEach
    void tearDown() {
        reset(r2FileManagerClient, r2ClientProperties);
    }

    // ==================== Cover Image Deletion Tests ====================

    @Test
    void shouldDeleteCoverImage_successfully() {
        // Arrange
        String coverImageKey = "events/1/cover/abc123.jpg";
        when(r2FileManagerClient.deleteFile(BUCKET_NAME, coverImageKey)).thenReturn(true);

        // Act
        boolean result = httpRepository.deleteCoverImage(coverImageKey);

        // Assert
        assertTrue(result);
        verify(r2FileManagerClient).deleteFile(BUCKET_NAME, coverImageKey);
    }

    @Test
    void shouldReturnFalse_whenCoverImageDeletionFails() {
        // Arrange
        String coverImageKey = "events/1/cover/abc123.jpg";
        when(r2FileManagerClient.deleteFile(BUCKET_NAME, coverImageKey)).thenReturn(false);

        // Act
        boolean result = httpRepository.deleteCoverImage(coverImageKey);

        // Assert
        assertFalse(result);
        verify(r2FileManagerClient).deleteFile(BUCKET_NAME, coverImageKey);
    }

    @Test
    void shouldReturnTrue_whenCoverImageKeyIsNull() {
        // Act
        boolean result = httpRepository.deleteCoverImage(null);

        // Assert
        assertTrue(result);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldReturnTrue_whenCoverImageKeyIsEmpty() {
        // Act
        boolean result = httpRepository.deleteCoverImage("");

        // Assert
        assertTrue(result);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldUseCorrectBucketName_forCoverImageDeletion() {
        // Arrange
        String coverImageKey = "events/1/cover/test.jpg";
        ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);
        when(r2FileManagerClient.deleteFile(bucketCaptor.capture(), eq(coverImageKey)))
                .thenReturn(true);

        // Act
        httpRepository.deleteCoverImage(coverImageKey);

        // Assert
        assertEquals(BUCKET_NAME, bucketCaptor.getValue());
    }

    // ==================== Gallery Images Deletion Tests ====================

    @Test
    void shouldDeleteGalleryImages_successfully() {
        // Arrange
        Set<String> galleryKeys = Set.of(
                "events/1/gallery/img1.jpg",
                "events/1/gallery/img2.jpg",
                "events/1/gallery/img3.jpg"
        );

        when(r2FileManagerClient.deleteFile(eq(BUCKET_NAME), anyString())).thenReturn(true);

        // Act
        boolean result = httpRepository.deleteGalleryImages(galleryKeys);

        // Assert
        assertTrue(result);
        verify(r2FileManagerClient, times(3)).deleteFile(eq(BUCKET_NAME), anyString());
    }

    @Test
    void shouldReturnFalse_whenAnyGalleryImageDeletionFails() {
        // Arrange
        String key1 = "events/1/gallery/img1.jpg";
        String key2 = "events/1/gallery/img2.jpg";
        String key3 = "events/1/gallery/img3.jpg";

        Set<String> galleryKeys = Set.of(key1, key2, key3);

        // First and third succeed, second fails
        when(r2FileManagerClient.deleteFile(BUCKET_NAME, key1)).thenReturn(true);
        when(r2FileManagerClient.deleteFile(BUCKET_NAME, key2)).thenReturn(false); // Failure
        when(r2FileManagerClient.deleteFile(BUCKET_NAME, key3)).thenReturn(true);

        // Act
        boolean result = httpRepository.deleteGalleryImages(galleryKeys);

        // Assert
        assertFalse(result); // Should return false because one deletion failed
        verify(r2FileManagerClient, times(3)).deleteFile(eq(BUCKET_NAME), anyString());
    }

    @Test
    void shouldReturnTrue_whenGalleryImageKeysIsNull() {
        // Act
        boolean result = httpRepository.deleteGalleryImages(null);

        // Assert
        assertTrue(result);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldReturnTrue_whenGalleryImageKeysIsEmpty() {
        // Act
        boolean result = httpRepository.deleteGalleryImages(Set.of());

        // Assert
        assertTrue(result);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldDeleteEachGalleryImage_individually() {
        // Arrange
        String key1 = "events/1/gallery/img1.jpg";
        String key2 = "events/1/gallery/img2.jpg";

        Set<String> galleryKeys = Set.of(key1, key2);

        when(r2FileManagerClient.deleteFile(anyString(), anyString())).thenReturn(true);

        // Act
        httpRepository.deleteGalleryImages(galleryKeys);

        // Assert
        verify(r2FileManagerClient).deleteFile(BUCKET_NAME, key1);
        verify(r2FileManagerClient).deleteFile(BUCKET_NAME, key2);
    }

    @Test
    void shouldContinueDeletingOtherImages_whenOneFails() {
        // Arrange - Verify that all deletions are attempted even if some fail
        String key1 = "events/1/gallery/img1.jpg";
        String key2 = "events/1/gallery/img2.jpg";
        String key3 = "events/1/gallery/img3.jpg";

        Set<String> galleryKeys = Set.of(key1, key2, key3);

        when(r2FileManagerClient.deleteFile(BUCKET_NAME, key1)).thenReturn(false); // Fails
        when(r2FileManagerClient.deleteFile(BUCKET_NAME, key2)).thenReturn(true);
        when(r2FileManagerClient.deleteFile(BUCKET_NAME, key3)).thenReturn(true);

        // Act
        httpRepository.deleteGalleryImages(galleryKeys);

        // Assert - All three deletions should be attempted
        verify(r2FileManagerClient).deleteFile(BUCKET_NAME, key1);
        verify(r2FileManagerClient).deleteFile(BUCKET_NAME, key2);
        verify(r2FileManagerClient).deleteFile(BUCKET_NAME, key3);
    }

    // ==================== Event Folder Deletion Tests ====================

    @Test
    void shouldDeleteEventFolder_successfully() {
        // Arrange
        Long eventId = 1L;
        String expectedFolderKey = "events/1/";

        when(r2FileManagerClient.deleteFolder(BUCKET_NAME, expectedFolderKey)).thenReturn(true);

        // Act
        boolean result = httpRepository.deleteEventFolder(eventId);

        // Assert
        assertTrue(result);
        verify(r2FileManagerClient).deleteFolder(BUCKET_NAME, expectedFolderKey);
    }

    @Test
    void shouldReturnFalse_whenEventFolderDeletionFails() {
        // Arrange
        Long eventId = 1L;
        String expectedFolderKey = "events/1/";

        when(r2FileManagerClient.deleteFolder(BUCKET_NAME, expectedFolderKey)).thenReturn(false);

        // Act
        boolean result = httpRepository.deleteEventFolder(eventId);

        // Assert
        assertFalse(result);
        verify(r2FileManagerClient).deleteFolder(BUCKET_NAME, expectedFolderKey);
    }

    @Test
    void shouldReturnFalse_whenEventIdIsNull() {
        // Act
        boolean result = httpRepository.deleteEventFolder(null);

        // Assert
        assertFalse(result);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldGenerateCorrectFolderKey_forEventId() {
        // Arrange
        Long eventId = 12345L;
        ArgumentCaptor<String> folderKeyCaptor = ArgumentCaptor.forClass(String.class);

        when(r2FileManagerClient.deleteFolder(eq(BUCKET_NAME), folderKeyCaptor.capture()))
                .thenReturn(true);

        // Act
        httpRepository.deleteEventFolder(eventId);

        // Assert
        assertEquals("events/12345/", folderKeyCaptor.getValue());
    }

    @Test
    void shouldIncludeTrailingSlash_inFolderKey() {
        // Arrange
        ArgumentCaptor<String> folderKeyCaptor = ArgumentCaptor.forClass(String.class);

        when(r2FileManagerClient.deleteFolder(eq(BUCKET_NAME), folderKeyCaptor.capture()))
                .thenReturn(true);

        // Act
        httpRepository.deleteEventFolder(999L);

        // Assert
        String capturedKey = folderKeyCaptor.getValue();
        assertTrue(capturedKey.endsWith("/"));
        assertEquals("events/999/", capturedKey);
    }

    @Test
    void shouldUseDifferentFolderKeys_forDifferentEventIds() {
        // Arrange
        when(r2FileManagerClient.deleteFolder(anyString(), anyString())).thenReturn(true);

        // Act
        httpRepository.deleteEventFolder(1L);
        httpRepository.deleteEventFolder(2L);
        httpRepository.deleteEventFolder(100L);

        // Assert
        verify(r2FileManagerClient).deleteFolder(BUCKET_NAME, "events/1/");
        verify(r2FileManagerClient).deleteFolder(BUCKET_NAME, "events/2/");
        verify(r2FileManagerClient).deleteFolder(BUCKET_NAME, "events/100/");
    }

    @Test
    void shouldUseCorrectBucketName_forFolderDeletion() {
        // Arrange
        ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);

        when(r2FileManagerClient.deleteFolder(bucketCaptor.capture(), anyString()))
                .thenReturn(true);

        // Act
        httpRepository.deleteEventFolder(1L);

        // Assert
        assertEquals(BUCKET_NAME, bucketCaptor.getValue());
    }

    // ==================== Integration/Edge Case Tests ====================

    @Test
    void shouldHandleLargeEventId() {
        // Arrange
        Long largeEventId = 9999999999L;
        String expectedFolderKey = "events/9999999999/";

        when(r2FileManagerClient.deleteFolder(BUCKET_NAME, expectedFolderKey)).thenReturn(true);

        // Act
        boolean result = httpRepository.deleteEventFolder(largeEventId);

        // Assert
        assertTrue(result);
        verify(r2FileManagerClient).deleteFolder(BUCKET_NAME, expectedFolderKey);
    }

    @Test
    void shouldHandleMultipleGalleryImages() {
        // Arrange
        Set<String> manyImages = Set.of(
                "events/1/gallery/img1.jpg",
                "events/1/gallery/img2.jpg",
                "events/1/gallery/img3.jpg",
                "events/1/gallery/img4.jpg",
                "events/1/gallery/img5.jpg"
        );

        when(r2FileManagerClient.deleteFile(eq(BUCKET_NAME), anyString())).thenReturn(true);

        // Act
        boolean result = httpRepository.deleteGalleryImages(manyImages);

        // Assert
        assertTrue(result);
        verify(r2FileManagerClient, times(5)).deleteFile(eq(BUCKET_NAME), anyString());
    }

    @Test
    void shouldReturnTrue_whenSingleGalleryImageDeletedSuccessfully() {
        // Arrange
        Set<String> singleImage = Set.of("events/1/gallery/img1.jpg");

        when(r2FileManagerClient.deleteFile(anyString(), anyString())).thenReturn(true);

        // Act
        boolean result = httpRepository.deleteGalleryImages(singleImage);

        // Assert
        assertTrue(result);
        verify(r2FileManagerClient, times(1)).deleteFile(anyString(), anyString());
    }
}
