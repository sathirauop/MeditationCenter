package com.isipathana.meditationcenter.rest.admin.book.get;

import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.isipathana.meditationcenter.jooq.Tables.BOOKS;

/**
 * Repository implementation for retrieving all books (admin view) using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetAdminBooksRepository implements GetAdminBooksDataAccess {

    private final DSLContext dslContext;

    @Override
    public List<Book> findAllBooks(int offset, int limit) {
        return dslContext
                .selectFrom(BOOKS)
                // No active filter - show all books for admin
                .orderBy(BOOKS.CREATED_AT.desc())
                .limit(limit)
                .offset(offset)
                .fetch(record -> Book.builder()
                        .bookId(record.get(BOOKS.BOOK_ID))
                        .title(record.get(BOOKS.TITLE))
                        .author(record.get(BOOKS.AUTHOR))
                        .description(record.get(BOOKS.DESCRIPTION))
                        .pdfFileKey(record.get(BOOKS.PDF_FILE_KEY))
                        .coverImageKey(record.get(BOOKS.COVER_IMAGE_KEY))
                        .isActive(record.get(BOOKS.IS_ACTIVE))
                        .createdAt(record.get(BOOKS.CREATED_AT))
                        .updatedAt(record.get(BOOKS.UPDATED_AT))
                        .build()
                );
    }

    @Override
    public long getAllBookCount() {
        return dslContext
                .selectCount()
                .from(BOOKS)
                // No filter - count all books
                .fetchOne(0, long.class);
    }
}
