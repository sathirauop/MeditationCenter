package com.isipathana.meditationcenter.rest.admin.book.get;

import com.isipathana.meditationcenter.records.book.Book;

import java.util.List;

/**
 * Data access interface for retrieving all books (admin view).
 *
 * @author Sathira Basnayake
 */
public interface GetAdminBooksDataAccess {

    /**
     * Find all books (active and inactive) with pagination.
     *
     * @param offset Starting row position
     * @param limit  Maximum number of results
     * @return List of all books
     */
    List<Book> findAllBooks(int offset, int limit);

    /**
     * Get total count of all books.
     *
     * @return Total count
     */
    long getAllBookCount();
}
