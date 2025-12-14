package com.isipathana.meditationcenter.rest.admin.book.post;

import org.springframework.web.multipart.MultipartFile;

/**
 * HTTP data access interface for book file uploads to R2 storage.
 *
 * @author Sathira Basnayake
 */
public interface PostBookHttpDataAccess {

    /**
     * Upload PDF file and optional cover image to R2 storage.
     *
     * @param bookId      Book ID for organizing files
     * @param pdfFile     PDF file to upload (required)
     * @param coverImage  Cover image file to upload (optional)
     * @return UploadResult containing file keys
     */
    UploadResult uploadBookFiles(Long bookId, MultipartFile pdfFile, MultipartFile coverImage);

    /**
     * Result of file upload operation.
     *
     * @param pdfFileKey      R2 key for uploaded PDF file
     * @param coverImageKey   R2 key for uploaded cover image (null if not provided)
     */
    record UploadResult(
            String pdfFileKey,
            String coverImageKey
    ) {}
}
