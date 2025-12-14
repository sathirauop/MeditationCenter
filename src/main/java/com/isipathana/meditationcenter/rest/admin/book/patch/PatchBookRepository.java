package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.BOOKS;

/**
 * Repository implementation for updating book information using jOOQ.
 * Performs partial updates - only updates fields that are non-null.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PatchBookRepository implements PatchBookDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<Book> findBookById(Long bookId) {
        return dslContext
                .selectFrom(BOOKS)
                .where(BOOKS.BOOK_ID.eq(bookId))
                .fetchOptional(record -> Book.builder()
                        .bookId(record.getBookId())
                        .title(record.getTitle())
                        .author(record.getAuthor())
                        .description(record.getDescription())
                        .pdfFileKey(record.getPdfFileKey())
                        .coverImageKey(record.getCoverImageKey())
                        .isActive(record.getIsActive())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build()
                );
    }

    @Override
    public Book updateBook(Book book) {
        // Build dynamic update query - only update non-null fields
        var updateStep = dslContext.update(BOOKS);
        UpdateSetMoreStep<?> query = null;

        if (book.title() != null) {
            query = updateStep.set(BOOKS.TITLE, book.title());
        }

        if (book.author() != null) {
            query = query != null
                    ? query.set(BOOKS.AUTHOR, book.author())
                    : updateStep.set(BOOKS.AUTHOR, book.author());
        }

        if (book.description() != null) {
            query = query != null
                    ? query.set(BOOKS.DESCRIPTION, book.description())
                    : updateStep.set(BOOKS.DESCRIPTION, book.description());
        }

        if (book.isActive() != null) {
            query = query != null
                    ? query.set(BOOKS.IS_ACTIVE, book.isActive())
                    : updateStep.set(BOOKS.IS_ACTIVE, book.isActive());
        }

        // Always update updated_at timestamp
        if (query != null) {
            query = query.set(BOOKS.UPDATED_AT, org.jooq.impl.DSL.currentLocalDateTime());
        }

        // Execute update if any fields were set
        if (query != null) {
            query.where(BOOKS.BOOK_ID.eq(book.bookId())).execute();
        }

        // Fetch and return updated book
        return findBookById(book.bookId())
                .orElseThrow(() -> new IllegalStateException("Book not found after update"));
    }
}
