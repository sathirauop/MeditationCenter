package com.isipathana.meditationcenter.rest.book.get;

/**
 * HTTP data access interface for generating presigned URLs for book files.
 *
 * @author Sathira Basnayake
 */
public interface GetBooksHttpDataAccess {

    /**
     * Generate presigned URL for a book file (PDF or cover image).
     *
     * @param fileKey R2 storage key
     * @return Presigned URL with expiry, or null if key is empty
     */
    String generatePresignedUrl(String fileKey);
}
