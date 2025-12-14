package com.isipathana.meditationcenter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service for validating uploaded PDF files using magic byte verification.
 * <p>
 * Validates that files are legitimate PDFs by checking file signatures (magic bytes)
 * rather than relying on file extensions, which can be easily spoofed.
 *
 * @author Sathira Basnayake
 */
@Service
public class PdfValidationService {

    private static final Logger logger = LoggerFactory.getLogger(PdfValidationService.class);

    // Maximum file size: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    // Magic bytes for PDF format: %PDF-
    private static final byte[] PDF_MAGIC_BYTES = {0x25, 0x50, 0x44, 0x46, 0x2D}; // %PDF-

    // PDF footer signature
    private static final String PDF_EOF_MARKER = "%%EOF";

    /**
     * Validates an uploaded PDF file.
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

        // Verify PDF magic bytes (header)
        try (InputStream inputStream = file.getInputStream()) {
            byte[] headerBytes = new byte[5]; // Read first 5 bytes for %PDF-
            int bytesRead = inputStream.read(headerBytes);

            if (bytesRead < PDF_MAGIC_BYTES.length) {
                return ValidationResult.failure("File is too small to be a valid PDF");
            }

            if (!isPdf(headerBytes)) {
                return ValidationResult.failure("File is not a valid PDF format");
            }

            logger.debug("Validated PDF file: {}", file.getOriginalFilename());
            return ValidationResult.success();

        } catch (IOException e) {
            logger.error("Failed to read file for validation: {}", file.getOriginalFilename(), e);
            return ValidationResult.failure("Failed to read file: " + e.getMessage());
        }
    }

    /**
     * Check if the header bytes match PDF magic bytes.
     *
     * @param header The first bytes of the file
     * @return true if the file starts with %PDF-, false otherwise
     */
    private boolean isPdf(byte[] header) {
        if (header.length < PDF_MAGIC_BYTES.length) {
            return false;
        }
        for (int i = 0; i < PDF_MAGIC_BYTES.length; i++) {
            if (header[i] != PDF_MAGIC_BYTES[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Result of PDF validation.
     * Reuses the same record structure as ImageValidationService for consistency.
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
