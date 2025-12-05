package com.isipathana.meditationcenter.rest.admin.event.post;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import com.isipathana.meditationcenter.service.ImageValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for PostEventHttpRepository.
 * Tests image upload operations to R2 storage with validation.
 * <p>
 * Pattern: Mock R2 client and validation service, test upload logic.
 * Reference: bow-products HttpRepository tests
 *
 * @author Sathira Basnayake
 */
@ExtendWith(MockitoExtension.class)
public class PostEventHttpRepositoryTest {

    private static final String BUCKET_NAME = "meditation-center-events";
    private static final Duration PRESIGNED_URL_TIMEOUT = Duration.ofMinutes(5);
    private static final R2ClientProperties.Bucket BUCKET = new R2ClientProperties.Bucket(BUCKET_NAME, PRESIGNED_URL_TIMEOUT);

    @Mock
    private R2FileManagerClient r2FileManagerClient;

    @Mock
    private ImageValidationService imageValidationService;

    @Mock
    private R2ClientProperties r2ClientProperties;

    @InjectMocks
    private PostEventHttpRepository httpRepository;

    @BeforeEach
    void setUp() {
        when(r2ClientProperties.buckets()).thenReturn(Map.of("events", BUCKET));
    }

    @AfterEach
    void tearDown() {
        reset(r2FileManagerClient, imageValidationService, r2ClientProperties);
    }

    @Test
    void shouldUploadCoverImage_whenValid() {
        // Arrange
        Long eventId = 1L;
        MultipartFile coverImage = mock(MultipartFile.class);
        when(coverImage.isEmpty()).thenReturn(false);
        when(coverImage.getOriginalFilename()).thenReturn("test-image.jpg");

        when(imageValidationService.validate(coverImage))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(eq(BUCKET_NAME), anyString(), eq(coverImage)))
                .thenReturn(true);

        // Act
        String imageKey = httpRepository.uploadCoverImage(eventId, coverImage);

        // Assert
        assertNotNull(imageKey);
        assertTrue(imageKey.startsWith("events/1/cover/"));
        assertTrue(imageKey.endsWith(".jpg"));

        verify(imageValidationService).validate(coverImage);
        verify(r2FileManagerClient).uploadFile(eq(BUCKET_NAME), anyString(), eq(coverImage));
    }

    @Test
    void shouldThrowException_whenCoverImageValidationFails() {
        // Arrange
        Long eventId = 1L;
        MultipartFile coverImage = mock(MultipartFile.class);
        when(coverImage.isEmpty()).thenReturn(false);

        when(imageValidationService.validate(coverImage))
                .thenReturn(ImageValidationService.ValidationResult.failure("File too large"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> httpRepository.uploadCoverImage(eventId, coverImage));

        assertEquals("Invalid cover image: File too large", exception.getMessage());
        verify(imageValidationService).validate(coverImage);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldThrowException_whenCoverImageUploadFails() {
        // Arrange
        Long eventId = 1L;
        MultipartFile coverImage = mock(MultipartFile.class);
        when(coverImage.isEmpty()).thenReturn(false);
        when(coverImage.getOriginalFilename()).thenReturn("test.jpg");

        when(imageValidationService.validate(coverImage))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(eq(BUCKET_NAME), anyString(), eq(coverImage)))
                .thenReturn(false); // Upload fails

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> httpRepository.uploadCoverImage(eventId, coverImage));

        assertEquals("Failed to upload cover image to R2", exception.getMessage());
        verify(r2FileManagerClient).uploadFile(eq(BUCKET_NAME), anyString(), eq(coverImage));
    }

    @Test
    void shouldReturnNull_whenCoverImageIsNull() {
        // Act
        String imageKey = httpRepository.uploadCoverImage(1L, null);

        // Assert
        assertNull(imageKey);
        verifyNoInteractions(imageValidationService);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldReturnNull_whenCoverImageIsEmpty() {
        // Arrange
        MultipartFile coverImage = mock(MultipartFile.class);
        when(coverImage.isEmpty()).thenReturn(true);

        // Act
        String imageKey = httpRepository.uploadCoverImage(1L, coverImage);

        // Assert
        assertNull(imageKey);
        verifyNoInteractions(imageValidationService);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldGenerateUniqueKeys_forCoverImages() {
        // Arrange
        MultipartFile coverImage = mock(MultipartFile.class);
        when(coverImage.isEmpty()).thenReturn(false);
        when(coverImage.getOriginalFilename()).thenReturn("test.jpg");

        when(imageValidationService.validate(any(MultipartFile.class))).thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(anyString(), anyString(), any(MultipartFile.class))).thenReturn(true);

        // Act - Upload twice
        String key1 = httpRepository.uploadCoverImage(1L, coverImage);
        String key2 = httpRepository.uploadCoverImage(1L, coverImage);

        // Assert - Keys should be different (contains UUID)
        assertNotEquals(key1, key2);
        assertTrue(key1.startsWith("events/1/cover/"));
        assertTrue(key2.startsWith("events/1/cover/"));
    }

    @Test
    void shouldUploadGalleryImages_whenValid() {
        // Arrange
        Long eventId = 1L;

        MultipartFile image1 = mock(MultipartFile.class);
        when(image1.isEmpty()).thenReturn(false);
        when(image1.getOriginalFilename()).thenReturn("gallery1.jpg");

        MultipartFile image2 = mock(MultipartFile.class);
        when(image2.isEmpty()).thenReturn(false);
        when(image2.getOriginalFilename()).thenReturn("gallery2.png");

        List<MultipartFile> galleryImages = List.of(image1, image2);

        when(imageValidationService.validate(any(MultipartFile.class)))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(eq(BUCKET_NAME), anyString(), any(MultipartFile.class)))
                .thenReturn(true);

        // Act
        List<String> uploadedKeys = httpRepository.uploadGalleryImages(eventId, galleryImages);

        // Assert
        assertEquals(2, uploadedKeys.size());
        assertTrue(uploadedKeys.get(0).startsWith("events/1/gallery/"));
        assertTrue(uploadedKeys.get(0).endsWith(".jpg"));
        assertTrue(uploadedKeys.get(1).startsWith("events/1/gallery/"));
        assertTrue(uploadedKeys.get(1).endsWith(".png"));

        verify(imageValidationService, times(2)).validate(any(MultipartFile.class));
        verify(r2FileManagerClient, times(2)).uploadFile(eq(BUCKET_NAME), anyString(), any(MultipartFile.class));
    }

    @Test
    void shouldThrowException_whenGalleryImageValidationFails() {
        // Arrange
        Long eventId = 1L;

        MultipartFile image1 = mock(MultipartFile.class);
        when(image1.isEmpty()).thenReturn(false);

        MultipartFile image2 = mock(MultipartFile.class);
        when(image2.isEmpty()).thenReturn(false);

        List<MultipartFile> galleryImages = List.of(image1, image2);

        // First image succeeds, second fails
        when(imageValidationService.validate(image1))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(imageValidationService.validate(image2))
                .thenReturn(ImageValidationService.ValidationResult.failure("Invalid format"));
        when(r2FileManagerClient.uploadFile(anyString(), anyString(), eq(image1)))
                .thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> httpRepository.uploadGalleryImages(eventId, galleryImages));

        assertTrue(exception.getMessage().contains("Invalid gallery image at index 1"));
        assertTrue(exception.getMessage().contains("Invalid format"));

        verify(imageValidationService, times(2)).validate(any());
        verify(r2FileManagerClient, times(1)).uploadFile(anyString(), anyString(), any(MultipartFile.class)); // Only first image uploaded
    }

    @Test
    void shouldThrowException_whenGalleryImageUploadFails() {
        // Arrange
        Long eventId = 1L;

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn("test.jpg");

        List<MultipartFile> galleryImages = List.of(image);

        when(imageValidationService.validate(image))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(eq(BUCKET_NAME), anyString(), eq(image)))
                .thenReturn(false); // Upload fails

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> httpRepository.uploadGalleryImages(eventId, galleryImages));

        assertTrue(exception.getMessage().contains("Failed to upload gallery image at index 0 to R2"));
    }

    @Test
    void shouldReturnEmptyList_whenGalleryImagesIsNull() {
        // Act
        List<String> uploadedKeys = httpRepository.uploadGalleryImages(1L, null);

        // Assert
        assertNotNull(uploadedKeys);
        assertTrue(uploadedKeys.isEmpty());
        verifyNoInteractions(imageValidationService);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldReturnEmptyList_whenGalleryImagesIsEmpty() {
        // Act
        List<String> uploadedKeys = httpRepository.uploadGalleryImages(1L, List.of());

        // Assert
        assertNotNull(uploadedKeys);
        assertTrue(uploadedKeys.isEmpty());
        verifyNoInteractions(imageValidationService);
        verifyNoInteractions(r2FileManagerClient);
    }

    @Test
    void shouldSkipEmptyGalleryImages() {
        // Arrange
        Long eventId = 1L;

        MultipartFile validImage = mock(MultipartFile.class);
        when(validImage.isEmpty()).thenReturn(false);
        when(validImage.getOriginalFilename()).thenReturn("valid.jpg");

        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);

        List<MultipartFile> galleryImages = List.of(validImage, emptyImage);

        when(imageValidationService.validate(validImage))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(eq(BUCKET_NAME), anyString(), eq(validImage)))
                .thenReturn(true);

        // Act
        List<String> uploadedKeys = httpRepository.uploadGalleryImages(eventId, galleryImages);

        // Assert
        assertEquals(1, uploadedKeys.size()); // Only valid image uploaded
        verify(imageValidationService, times(1)).validate(validImage);
        verify(r2FileManagerClient, times(1)).uploadFile(anyString(), anyString(), any(MultipartFile.class));
    }

    @Test
    void shouldExtractFileExtension_correctly() {
        // Arrange
        MultipartFile jpgImage = mock(MultipartFile.class);
        when(jpgImage.isEmpty()).thenReturn(false);
        when(jpgImage.getOriginalFilename()).thenReturn("photo.jpg");

        MultipartFile pngImage = mock(MultipartFile.class);
        when(pngImage.isEmpty()).thenReturn(false);
        when(pngImage.getOriginalFilename()).thenReturn("image.png");

        MultipartFile webpImage = mock(MultipartFile.class);
        when(webpImage.isEmpty()).thenReturn(false);
        when(webpImage.getOriginalFilename()).thenReturn("graphic.WEBP"); // Uppercase

        when(imageValidationService.validate(any()))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(anyString(), anyString(), any(MultipartFile.class)))
                .thenReturn(true);

        // Act
        String jpgKey = httpRepository.uploadCoverImage(1L, jpgImage);
        String pngKey = httpRepository.uploadCoverImage(1L, pngImage);
        String webpKey = httpRepository.uploadCoverImage(1L, webpImage);

        // Assert
        assertTrue(jpgKey.endsWith(".jpg"));
        assertTrue(pngKey.endsWith(".png"));
        assertTrue(webpKey.endsWith(".webp")); // Should be lowercase
    }

    @Test
    void shouldDefaultToJpg_whenNoExtension() {
        // Arrange
        MultipartFile noExtImage = mock(MultipartFile.class);
        when(noExtImage.isEmpty()).thenReturn(false);
        when(noExtImage.getOriginalFilename()).thenReturn("imagefile");

        when(imageValidationService.validate(any()))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(anyString(), anyString(), any(MultipartFile.class)))
                .thenReturn(true);

        // Act
        String imageKey = httpRepository.uploadCoverImage(1L, noExtImage);

        // Assert
        assertTrue(imageKey.endsWith(".jpg")); // Defaults to jpg
    }

    @Test
    void shouldDefaultToJpg_whenFilenameIsNull() {
        // Arrange
        MultipartFile nullNameImage = mock(MultipartFile.class);
        when(nullNameImage.isEmpty()).thenReturn(false);
        when(nullNameImage.getOriginalFilename()).thenReturn(null);

        when(imageValidationService.validate(any()))
                .thenReturn(ImageValidationService.ValidationResult.success());
        when(r2FileManagerClient.uploadFile(anyString(), anyString(), any(MultipartFile.class)))
                .thenReturn(true);

        // Act
        String imageKey = httpRepository.uploadCoverImage(1L, nullNameImage);

        // Assert
        assertTrue(imageKey.endsWith(".jpg"));
    }

    @Test
    void shouldGenerateCorrectKeyFormat() {
        // Arrange
        MultipartFile coverImage = mock(MultipartFile.class);
        when(coverImage.isEmpty()).thenReturn(false);
        when(coverImage.getOriginalFilename()).thenReturn("test.jpg");

        when(imageValidationService.validate(any()))
                .thenReturn(ImageValidationService.ValidationResult.success());

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        when(r2FileManagerClient.uploadFile(eq(BUCKET_NAME), keyCaptor.capture(), eq(coverImage)))
                .thenReturn(true);

        // Act
        httpRepository.uploadCoverImage(123L, coverImage);

        // Assert
        String capturedKey = keyCaptor.getValue();
        assertTrue(capturedKey.matches("events/123/cover/[a-f0-9\\-]+\\.jpg"));
    }

    @Test
    void shouldGenerateCorrectGalleryKeyFormat() {
        // Arrange
        MultipartFile galleryImage = mock(MultipartFile.class);
        when(galleryImage.isEmpty()).thenReturn(false);
        when(galleryImage.getOriginalFilename()).thenReturn("gallery.png");

        when(imageValidationService.validate(any()))
                .thenReturn(ImageValidationService.ValidationResult.success());

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        when(r2FileManagerClient.uploadFile(eq(BUCKET_NAME), keyCaptor.capture(), eq(galleryImage)))
                .thenReturn(true);

        // Act
        httpRepository.uploadGalleryImages(456L, List.of(galleryImage));

        // Assert
        String capturedKey = keyCaptor.getValue();
        assertTrue(capturedKey.matches("events/456/gallery/[a-f0-9\\-]+\\.png"));
    }

    @Test
    void shouldUseCorrectBucketName() {
        // Arrange
        MultipartFile coverImage = mock(MultipartFile.class);
        when(coverImage.isEmpty()).thenReturn(false);
        when(coverImage.getOriginalFilename()).thenReturn("test.jpg");

        when(imageValidationService.validate(any()))
                .thenReturn(ImageValidationService.ValidationResult.success());

        ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);
        when(r2FileManagerClient.uploadFile(bucketCaptor.capture(), anyString(), any(MultipartFile.class)))
                .thenReturn(true);

        // Act
        httpRepository.uploadCoverImage(1L, coverImage);

        // Assert
        assertEquals(BUCKET_NAME, bucketCaptor.getValue());
    }
}
