package com.isipathana.meditationcenter.rest.admin.book.patch;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for updating book information (partial update).
 * Only updates fields that are provided in the request.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class PatchBookUseCase implements UseCase<PatchBookRequest, PatchBookResponse> {

    private final PatchBookDataAccess repository;
    private final PatchBookResponseBuilder presenter;

    /**
     * Execute the book update operation.
     *
     * @param bookId  ID of the book to update
     * @param request Request containing fields to update (all optional)
     * @return Response with updated book details and presigned URLs
     * @throws ResourceNotFoundException if book with given ID doesn't exist
     */
    @Transactional
    public PatchBookResponse execute(Long bookId, PatchBookRequest request) {
        // 1. Verify book exists
        Book existingBook = repository.findBookById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Book not found with ID: " + bookId
                ));

        // 2. Validate that at least one field is being updated
        if (request.title() == null &&
            request.author() == null &&
            request.description() == null &&
            request.isActive() == null) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        // 3. Build book object with only the fields to update
        Book bookToUpdate = Book.builder()
                .bookId(bookId)
                .title(request.title())
                .author(request.author())
                .description(request.description())
                .isActive(request.isActive())
                .build();

        // 4. Update book in database
        Book updatedBook = repository.updateBook(bookToUpdate);

        // 5. Build and return response using presenter
        return presenter.build(updatedBook);
    }

    @Override
    public PatchBookResponse handle(PatchBookRequest request) {
        throw new UnsupportedOperationException(
                "Use execute(Long bookId, PatchBookRequest request) instead"
        );
    }
}
