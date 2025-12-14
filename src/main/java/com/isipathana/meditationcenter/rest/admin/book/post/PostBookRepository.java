package com.isipathana.meditationcenter.rest.admin.book.post;

import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.BOOKS;

/**
 * Repository implementation for creating books using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostBookRepository implements PostBookDataAccess {

    private final DSLContext dslContext;

    @Override
    public Book saveBook(Book book) {
        var record = dslContext
                .insertInto(BOOKS)
                .set(BOOKS.TITLE, book.title())
                .set(BOOKS.AUTHOR, book.author())
                .set(BOOKS.DESCRIPTION, book.description())
                .set(BOOKS.PDF_FILE_KEY, book.pdfFileKey())
                .set(BOOKS.COVER_IMAGE_KEY, book.coverImageKey())
                .set(BOOKS.IS_ACTIVE, book.isActive() != null ? book.isActive() : true)
                .returning(
                        BOOKS.BOOK_ID,
                        BOOKS.TITLE,
                        BOOKS.AUTHOR,
                        BOOKS.DESCRIPTION,
                        BOOKS.PDF_FILE_KEY,
                        BOOKS.COVER_IMAGE_KEY,
                        BOOKS.IS_ACTIVE,
                        BOOKS.CREATED_AT,
                        BOOKS.UPDATED_AT
                )
                .fetchOne();

        return Book.builder()
                .bookId(record.get(BOOKS.BOOK_ID))
                .title(record.get(BOOKS.TITLE))
                .author(record.get(BOOKS.AUTHOR))
                .description(record.get(BOOKS.DESCRIPTION))
                .pdfFileKey(record.get(BOOKS.PDF_FILE_KEY))
                .coverImageKey(record.get(BOOKS.COVER_IMAGE_KEY))
                .isActive(record.get(BOOKS.IS_ACTIVE))
                .createdAt(record.get(BOOKS.CREATED_AT))
                .updatedAt(record.get(BOOKS.UPDATED_AT))
                .build();
    }

    @Override
    public Book updateFileKeys(Long bookId, String pdfFileKey, String coverImageKey) {
        var record = dslContext
                .update(BOOKS)
                .set(BOOKS.PDF_FILE_KEY, pdfFileKey)
                .set(BOOKS.COVER_IMAGE_KEY, coverImageKey)
                .where(BOOKS.BOOK_ID.eq(bookId))
                .returning(
                        BOOKS.BOOK_ID,
                        BOOKS.TITLE,
                        BOOKS.AUTHOR,
                        BOOKS.DESCRIPTION,
                        BOOKS.PDF_FILE_KEY,
                        BOOKS.COVER_IMAGE_KEY,
                        BOOKS.IS_ACTIVE,
                        BOOKS.CREATED_AT,
                        BOOKS.UPDATED_AT
                )
                .fetchOne();

        return Book.builder()
                .bookId(record.get(BOOKS.BOOK_ID))
                .title(record.get(BOOKS.TITLE))
                .author(record.get(BOOKS.AUTHOR))
                .description(record.get(BOOKS.DESCRIPTION))
                .pdfFileKey(record.get(BOOKS.PDF_FILE_KEY))
                .coverImageKey(record.get(BOOKS.COVER_IMAGE_KEY))
                .isActive(record.get(BOOKS.IS_ACTIVE))
                .createdAt(record.get(BOOKS.CREATED_AT))
                .updatedAt(record.get(BOOKS.UPDATED_AT))
                .build();
    }
}
