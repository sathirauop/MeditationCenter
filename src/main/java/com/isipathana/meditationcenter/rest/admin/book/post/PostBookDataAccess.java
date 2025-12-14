package com.isipathana.meditationcenter.rest.admin.book.post;

import com.isipathana.meditationcenter.records.book.Book;

/**
 * Data access interface for creating books.
 *
 * @author Sathira Basnayake
 */
public interface PostBookDataAccess {

    /**
     * Save a new book to the database.
     *
     * @param book Book to save
     * @return Saved book with generated ID and timestamps
     */
    Book saveBook(Book book);

    /**
     * Update book file keys after upload.
     *
     * @param bookId         Book ID
     * @param pdfFileKey     PDF file key from R2 upload
     * @param coverImageKey  Cover image key from R2 upload (nullable)
     * @return Updated book
     */
    Book updateFileKeys(Long bookId, String pdfFileKey, String coverImageKey);
}
