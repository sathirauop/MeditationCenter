package com.isipathana.meditationcenter.rest.admin.book.patch;

/**
 * HTTP data access interface for generating presigned URLs for book files.
 * Used to generate temporary download URLs for PDFs and cover images.
 *
 * @author Sathira Basnayake
 */
public interface PatchBookHttpDataAccess {

    /**
     * Generate a presigned URL for accessing a file in R2 storage.
     *
     * @param fileKey The R2 object key for the file
     * @return Presigned URL valid for configured duration, or null if fileKey is null/empty
     */
    String generatePresignedUrl(String fileKey);
}
