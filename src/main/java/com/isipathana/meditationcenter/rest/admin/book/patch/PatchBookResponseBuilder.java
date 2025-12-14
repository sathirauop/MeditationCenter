package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.isipathana.meditationcenter.records.book.Book;

/**
 * Response builder interface for transforming book domain objects to PATCH response DTOs.
 *
 * @author Sathira Basnayake
 */
public interface PatchBookResponseBuilder {

    /**
     * Build a PATCH response from an updated book domain object.
     *
     * @param book Updated book domain object
     * @return PatchBookResponse with book details and presigned URLs
     */
    PatchBookResponse build(Book book);
}
