package com.isipathana.meditationcenter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service for validating uploaded image files using magic byte verification.
 * <p>
 * Validates that files are legitimate images by checking file signatures (magic bytes)
 * rather than relying on file extensions, which can be easily spoofed.
 * <p>
 * Supported formats: JPEG, PNG, GIF, WebP
 *
 * @author Claude Code
 */
@Service
public class ImageValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ImageValidationService.class);

    // Maximum file size: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // Magic bytes for supported image formats
    private static final byte[] JPEG_MAGIC_BYTES = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC_BYTES = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] GIF_MAGIC_BYTES_87A = {0x47, 0x49, 0x46, 0x38, 0x37, 0x61}; // GIF87a
    private static final byte[] GIF_MAGIC_BYTES_89A = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61}; // GIF89a
    private static final byte[] WEBP_MAGIC_BYTES = {0x52, 0x49, 0x46, 0x46}; // RIFF (WebP starts with RIFF)

    /**
     * Validates an uploaded image file.
     *
     * @param file The multipart file to validate
     * @return ValidationResult containing success status and error message if applicable
     */
    public ValidationResult validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ValidationResult.failure("File is empty or null");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            return ValidationResult.failure(
                    String.format("File size exceeds maximum limit of %d MB", MAX_FILE_SIZE / (1024 * 1024))
            );
        }

        // Verify magic bytes
        try (InputStream inputStream = file.getInputStream()) {
            byte[] headerBytes = new byte[12]; // Read first 12 bytes (enough for all supported formats)
            int bytesRead = inputStream.read(headerBytes);

            if (bytesRead < 4) {
                return ValidationResult.failure("File is too small to be a valid image");
            }

            if (isJpeg(headerBytes)) {
                logger.debug("Validated JPEG image: {}", file.getOriginalFilename());
                return ValidationResult.success();
            }

            if (isPng(headerBytes)) {
                logger.debug("Validated PNG image: {}", file.getOriginalFilename());
                return ValidationResult.success();
            }

            if (isGif(headerBytes)) {
                logger.debug("Validated GIF image: {}", file.getOriginalFilename());
                return ValidationResult.success();
            }

            if (isWebP(headerBytes, inputStream)) {
                logger.debug("Validated WebP image: {}", file.getOriginalFilename());
                return ValidationResult.success();
            }

            return ValidationResult.failure("File is not a supported image format (JPEG, PNG, GIF, WebP)");

        } catch (IOException e) {
            logger.error("Failed to read file for validation: {}", file.getOriginalFilename(), e);
            return ValidationResult.failure("Failed to read file: " + e.getMessage());
        }
    }

    private boolean isJpeg(byte[] header) {
        if (header.length < JPEG_MAGIC_BYTES.length) {
            return false;
        }
        for (int i = 0; i < JPEG_MAGIC_BYTES.length; i++) {
            if (header[i] != JPEG_MAGIC_BYTES[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isPng(byte[] header) {
        if (header.length < PNG_MAGIC_BYTES.length) {
            return false;
        }
        for (int i = 0; i < PNG_MAGIC_BYTES.length; i++) {
            if (header[i] != PNG_MAGIC_BYTES[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isGif(byte[] header) {
        if (header.length < GIF_MAGIC_BYTES_87A.length) {
            return false;
        }

        // Check for GIF87a
        boolean isGif87a = true;
        for (int i = 0; i < GIF_MAGIC_BYTES_87A.length; i++) {
            if (header[i] != GIF_MAGIC_BYTES_87A[i]) {
                isGif87a = false;
                break;
            }
        }
        if (isGif87a) return true;

        // Check for GIF89a
        boolean isGif89a = true;
        for (int i = 0; i < GIF_MAGIC_BYTES_89A.length; i++) {
            if (header[i] != GIF_MAGIC_BYTES_89A[i]) {
                isGif89a = false;
                break;
            }
        }
        return isGif89a;
    }

    private boolean isWebP(byte[] header, InputStream inputStream) throws IOException {
        // WebP format: RIFF....WEBP
        // Check for RIFF header
        if (header.length < 12) {
            return false;
        }

        // Check RIFF magic bytes
        for (int i = 0; i < WEBP_MAGIC_BYTES.length; i++) {
            if (header[i] != WEBP_MAGIC_BYTES[i]) {
                return false;
            }
        }

        // Check for WEBP signature at offset 8
        byte[] webpSignature = {0x57, 0x45, 0x42, 0x50}; // "WEBP"
        for (int i = 0; i < webpSignature.length; i++) {
            if (header[8 + i] != webpSignature[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Result of image validation.
     */
    public record ValidationResult(
            boolean valid,
            String errorMessage
    ) {
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
    }
}
