package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.isipathana.meditationcenter.records.book.Book;

import java.util.Optional;

/**
 * Data access interface for updating book information.
 *
 * @author Sathira Basnayake
 */
public interface PatchBookDataAccess {

    /**
     * Find a book by its ID.
     *
     * @param bookId The book ID to search for
     * @return Optional containing the book if found, empty otherwise
     */
    Optional<Book> findBookById(Long bookId);

    /**
     * Update book with provided fields (partial update).
     * Only non-null fields in the book object will be updated.
     *
     * @param book Book object with bookId and fields to update
     * @return Updated book with all current values
     */
    Book updateBook(Book book);
}
