package com.isipathana.meditationcenter.rest.admin.book.post;

import com.isipathana.meditationcenter.records.book.Book;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * UseCase for creating books with PDF and optional cover image uploads.
 * Orchestrates file upload and database persistence.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class PostBookUseCase {

    private static final Logger logger = LoggerFactory.getLogger(PostBookUseCase.class);

    private final PostBookDataAccess repository;
    private final PostBookResponseBuilder presenter;

    @Autowired(required = false)
    private PostBookHttpDataAccess httpRepository;

    /**
     * Execute book creation with file uploads.
     *
     * @param request    Book metadata (title, author, description)
     * @param pdfFile    PDF file to upload (required)
     * @param coverImage Cover image file to upload (optional)
     * @return Response with book details and presigned URLs
     */
    @Transactional
    public PostBookResponse execute(PostBookRequest request, MultipartFile pdfFile, MultipartFile coverImage) {
        // Validate PDF file is provided
        if (pdfFile == null || pdfFile.isEmpty()) {
            throw new IllegalArgumentException("PDF file is required");
        }

        // First, create the book in database to get the book ID (needed for file upload paths)
        Book bookToCreate = Book.builder()
                .title(request.title())
                .author(request.author())
                .description(request.description())
                .pdfFileKey("placeholder") // Temporary placeholder to satisfy NOT NULL constraint
                .coverImageKey(null)
                .isActive(true)
                .build();

        Book createdBook = repository.saveBook(bookToCreate);
        logger.info("Created book with ID: {}", createdBook.bookId());

        // Upload files if R2 is enabled
        String pdfFileKey = "placeholder";
        String coverImageKey = null;

        if (httpRepository != null) {
            try {
                PostBookHttpDataAccess.UploadResult uploadResult =
                        httpRepository.uploadBookFiles(createdBook.bookId(), pdfFile, coverImage);

                pdfFileKey = uploadResult.pdfFileKey();
                coverImageKey = uploadResult.coverImageKey();

                logger.info("Uploaded files for book {}: PDF={}, Cover={}",
                        createdBook.bookId(), pdfFileKey, coverImageKey);

                // Update book record in database with actual file keys
                Book updatedBook = repository.updateFileKeys(
                        createdBook.bookId(),
                        pdfFileKey,
                        coverImageKey
                );

                // Build and return response
                return presenter.build(updatedBook);

            } catch (Exception e) {
                logger.error("Failed to upload files for book {}: {}", createdBook.bookId(), e.getMessage());
                throw new RuntimeException("Failed to upload book files: " + e.getMessage(), e);
            }
        } else {
            logger.warn("R2 is not enabled. Book created with placeholder file key.");
            // Build and return response with placeholder
            return presenter.build(createdBook);
        }
    }
}
