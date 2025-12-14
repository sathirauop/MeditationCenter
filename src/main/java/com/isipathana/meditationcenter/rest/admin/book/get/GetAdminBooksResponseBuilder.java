package com.isipathana.meditationcenter.rest.admin.book.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.book.Book;

import java.util.stream.Stream;

/**
 * Response builder interface for admin book list responses.
 *
 * @author Sathira Basnayake
 */
public interface GetAdminBooksResponseBuilder {

    /**
     * Build paginated response from stream of books.
     *
     * @param books         Stream of book domain objects
     * @param currentOffset Current pagination offset
     * @param maxOffset     Maximum offset (total count)
     * @return Paginated response with book DTOs and presigned URLs
     */
    OffsetSearchResponse<GetAdminBooksResponse> build(
            Stream<Book> books,
            long currentOffset,
            long maxOffset
    );
}
