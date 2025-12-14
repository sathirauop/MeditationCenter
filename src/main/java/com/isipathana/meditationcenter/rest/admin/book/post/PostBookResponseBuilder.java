package com.isipathana.meditationcenter.rest.admin.book.post;

import com.isipathana.meditationcenter.records.book.Book;

/**
 * Response builder interface for created book.
 *
 * @author Sathira Basnayake
 */
public interface PostBookResponseBuilder {

    /**
     * Build response from created book.
     *
     * @param book Created book domain object
     * @return Book response with presigned URLs
     */
    PostBookResponse build(Book book);
}
