package com.isipathana.meditationcenter.rest.book.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.book.Book;

import java.util.stream.Stream;

/**
 * Response builder interface for book list responses.
 *
 * @author Sathira Basnayake
 */
public interface GetBooksResponseBuilder {

    /**
     * Build paginated response from stream of books.
     *
     * @param books         Stream of book domain objects
     * @param currentOffset Current pagination offset
     * @param maxOffset     Maximum offset (total count)
     * @return Paginated response with book DTOs and presigned URLs
     */
    OffsetSearchResponse<GetBooksResponse> build(
            Stream<Book> books,
            long currentOffset,
            long maxOffset
    );
}
