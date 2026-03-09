package com.isipathana.meditationcenter.rest.admin.gallery.addPhotos;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import com.isipathana.meditationcenter.service.ImageValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository implementation for uploading gallery photos to R2 storage.
 * Only active when r2.enabled=true.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class AddGalleryPhotosHttpRepository implements AddGalleryPhotosHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(AddGalleryPhotosHttpRepository.class);

    private final R2FileManagerClient r2FileManagerClient;
    private final ImageValidationService imageValidationService;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public List<String> uploadPhotos(Long groupId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<String> uploadedKeys = new ArrayList<>();
        String bucketName = r2ClientProperties.buckets().get("gallery").bucketName();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            if (file.isEmpty()) {
                logger.warn("Skipping empty photo at index {} for gallery group {}", i, groupId);
                continue;
            }

            // Validate image
            ImageValidationService.ValidationResult result = imageValidationService.validate(file);
            if (!result.valid()) {
                logger.error("Photo validation failed at index {} for gallery group {}: {}", i, groupId,
                        result.errorMessage());
                throw new IllegalArgumentException(
                        String.format("Invalid photo at index %d: %s", i, result.errorMessage()));
            }

            // Generate unique key
            String key = generateImageKey(groupId, file.getOriginalFilename());

            // Upload to R2
            boolean success = r2FileManagerClient.uploadFile(bucketName, key, file);
            if (!success) {
                logger.error("Failed to upload photo at index {} to R2 for gallery group {}", i, groupId);
                throw new RuntimeException(String.format("Failed to upload photo at index %d to R2", i));
            }

            uploadedKeys.add(key);
            logger.info("Successfully uploaded photo {} for gallery group {}: {}", i, groupId, key);
        }

        return uploadedKeys;
    }

    /**
     * Generates a unique R2 object key for a gallery photo.
     * Format: gallery/{groupId}/{uuid}.{extension}
     */
    private String generateImageKey(Long groupId, String filename) {
        String extension = extractFileExtension(filename);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("gallery/%d/%s.%s", groupId, uniqueId, extension);
    }

    private String extractFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "jpg";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "jpg";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}
