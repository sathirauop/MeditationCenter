package com.isipathana.meditationcenter.rest.admin.book.post;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import com.isipathana.meditationcenter.service.ImageValidationService;
import com.isipathana.meditationcenter.service.PdfValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Repository implementation for uploading book files (PDF + cover image) to R2 storage.
 * <p>
 * Only active when r2.enabled=true in application.properties.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
public class PostBookHttpRepository implements PostBookHttpDataAccess {

    private static final Logger logger = LoggerFactory.getLogger(PostBookHttpRepository.class);

    private final R2FileManagerClient r2FileManagerClient;
    private final PdfValidationService pdfValidationService;
    private final ImageValidationService imageValidationService;
    private final R2ClientProperties r2ClientProperties;

    @Override
    public UploadResult uploadBookFiles(Long bookId, MultipartFile pdfFile, MultipartFile coverImage) {
        // Validate and upload PDF (required)
        String pdfFileKey = uploadPdfFile(bookId, pdfFile);

        // Validate and upload cover image (optional)
        String coverImageKey = uploadCoverImage(bookId, coverImage);

        return new UploadResult(pdfFileKey, coverImageKey);
    }

    /**
     * Upload PDF file to R2 storage.
     *
     * @param bookId  Book ID for organizing files
     * @param pdfFile PDF file to upload
     * @return R2 key for uploaded PDF
     */
    private String uploadPdfFile(Long bookId, MultipartFile pdfFile) {
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required");
        }

        // Validate PDF
        PdfValidationService.ValidationResult validationResult = pdfValidationService.validate(pdfFile);
        if (!validationResult.valid()) {
            logger.error("PDF validation failed for book {}: {}", bookId, validationResult.errorMessage());
            throw new IllegalArgumentException("Invalid PDF file: " + validationResult.errorMessage());
        }

        // Generate unique key: books/{bookId}/pdf/{uuid}.pdf
        String key = generatePdfKey(bookId, pdfFile.getOriginalFilename());

        // Get bucket name
        String bucketName = r2ClientProperties.buckets().get("books").bucketName();

        // Upload to R2
        boolean success = r2FileManagerClient.uploadFile(bucketName, key, pdfFile);

        if (!success) {
            logger.error("Failed to upload PDF to R2 for book {}", bookId);
            throw new RuntimeException("Failed to upload PDF file to R2");
        }

        logger.info("Successfully uploaded PDF for book {}: {}", bookId, key);
        return key;
    }

    /**
     * Upload cover image to R2 storage.
     *
     * @param bookId     Book ID for organizing files
     * @param coverImage Cover image file (optional)
     * @return R2 key for uploaded cover image, or null if not provided
     */
    private String uploadCoverImage(Long bookId, MultipartFile coverImage) {
        if (coverImage == null || coverImage.isEmpty()) {
            logger.debug("No cover image provided for book {}", bookId);
            return null;
        }

        // Validate image
        ImageValidationService.ValidationResult validationResult = imageValidationService.validate(coverImage);
        if (!validationResult.valid()) {
            logger.error("Cover image validation failed for book {}: {}", bookId, validationResult.errorMessage());
            throw new IllegalArgumentException("Invalid cover image: " + validationResult.errorMessage());
        }

        // Generate unique key: books/{bookId}/cover/{uuid}.{ext}
        String key = generateImageKey(bookId, "cover", coverImage.getOriginalFilename());

        // Get bucket name
        String bucketName = r2ClientProperties.buckets().get("books").bucketName();

        // Upload to R2
        boolean success = r2FileManagerClient.uploadFile(bucketName, key, coverImage);

        if (!success) {
            logger.error("Failed to upload cover image to R2 for book {}", bookId);
            throw new RuntimeException("Failed to upload cover image to R2");
        }

        logger.info("Successfully uploaded cover image for book {}: {}", bookId, key);
        return key;
    }

    /**
     * Generate unique key for PDF file.
     * Format: books/{bookId}/pdf/{uuid}.pdf
     */
    private String generatePdfKey(Long bookId, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return String.format("books/%d/pdf/%s%s", bookId, uuid, extension);
    }

    /**
     * Generate unique key for image file.
     * Format: books/{bookId}/{type}/{uuid}.{ext}
     */
    private String generateImageKey(Long bookId, String type, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return String.format("books/%d/%s/%s%s", bookId, type, uuid, extension);
    }

    /**
     * Extract file extension from filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex) : "";
    }
}
