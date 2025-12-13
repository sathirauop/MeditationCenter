package com.isipathana.meditationcenter.rest.admin.program.post;

import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * UseCase for creating new meditation programs.
 * Handles business logic for program creation and image uploads.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostProgramUseCase {

    private final PostProgramDataAccess repository;

    @Autowired(required = false)
    private PostProgramHttpDataAccess httpRepository;

    /**
     * Creates a new meditation program with images (multipart).
     *
     * @param request       The program creation request
     * @param coverImage    Optional cover image
     * @param galleryImages Optional gallery images
     * @return The created program response
     */
    @Transactional
    public PostProgramResponse execute(PostProgramRequest request, MultipartFile coverImage, List<MultipartFile> galleryImages) {
        log.info("Creating new meditation program: {}", request.name());

        // Build program domain object without images
        MeditationProgram program = MeditationProgram.builder()
                .name(request.name())
                .description(request.description())
                .maxSeats(request.maxSeats() != null ? request.maxSeats() : 0)
                .isActive(request.isActive() != null ? request.isActive() : true)
                .build();

        // Create program first to get ID
        MeditationProgram createdProgram = repository.createProgram(program);
        log.info("Program created with ID: {}", createdProgram.meditationProgramId());

        // Upload images to R2 if enabled and provided
        String coverImageKey = null;
        Set<String> galleryImageKeys = new HashSet<>();

        if (httpRepository != null) {
            // Upload cover image
            if (coverImage != null && !coverImage.isEmpty()) {
                log.info("Uploading cover image for program {}", createdProgram.meditationProgramId());
                coverImageKey = httpRepository.uploadCoverImage(createdProgram.meditationProgramId(), coverImage);
            }

            // Upload gallery images
            if (galleryImages != null && !galleryImages.isEmpty()) {
                log.info("Uploading {} gallery images for program {}", galleryImages.size(), createdProgram.meditationProgramId());
                List<String> uploadedKeys = httpRepository.uploadGalleryImages(createdProgram.meditationProgramId(), galleryImages);
                galleryImageKeys.addAll(uploadedKeys);
            }

            // Update program with image keys if any were uploaded
            if (coverImageKey != null || !galleryImageKeys.isEmpty()) {
                log.info("Updating program {} with image keys", createdProgram.meditationProgramId());
                createdProgram = repository.updateImageKeys(
                        createdProgram.meditationProgramId(),
                        coverImageKey,
                        galleryImageKeys.isEmpty() ? null : galleryImageKeys
                );
            }
        } else {
            log.info("R2 is disabled, skipping image uploads");
        }

        log.info("Program creation completed successfully: {}", createdProgram.meditationProgramId());

        // Build and return response
        return PostProgramResponse.builder()
                .meditationProgramId(createdProgram.meditationProgramId())
                .name(createdProgram.name())
                .description(createdProgram.description())
                .maxSeats(createdProgram.maxSeats())
                .coverImageKey(createdProgram.coverImageKey())
                .galleryImageKeys(createdProgram.galleryImageKeys())
                .isActive(createdProgram.isActive())
                .createdAt(createdProgram.createdAt())
                .updatedAt(createdProgram.updatedAt())
                .build();
    }

    /**
     * Creates a new meditation program without images (JSON only).
     * Backward compatibility method.
     *
     * @param request The program creation request
     * @return The created program response
     */
    @Transactional
    public PostProgramResponse execute(PostProgramRequest request) {
        return execute(request, null, null);
    }
}
