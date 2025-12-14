package com.isipathana.meditationcenter.rest.book.get;

import com.isipathana.meditationcenter.records.book.Book;

import java.util.List;

/**
 * Data access interface for retrieving books.
 *
 * @author Sathira Basnayake
 */
public interface GetBooksDataAccess {

    /**
     * Find active books with pagination.
     *
     * @param offset Starting row position
     * @param limit  Maximum number of results
     * @return List of active books
     */
    List<Book> findActiveBooks(int offset, int limit);

    /**
     * Get total count of active books.
     *
     * @return Total count
     */
    long getActiveBookCount();
}
