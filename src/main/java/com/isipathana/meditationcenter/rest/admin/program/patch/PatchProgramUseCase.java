package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
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
 * UseCase for updating meditation programs.
 * Handles business logic for partial program updates including image uploads.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchProgramUseCase {

    private final PatchProgramDataAccess repository;
    private final PatchProgramResponseBuilder responseBuilder;

    @Autowired(required = false)
    private PatchProgramHttpDataAccess httpRepository;

    /**
     * Update a program with optional image uploads.
     */
    @Transactional
    public PatchProgramResponse execute(Long programId, PatchProgramRequest request,
            MultipartFile coverImage, List<MultipartFile> galleryImages) {
        log.info("Updating meditation program with ID: {}", programId);

        // Verify program exists
        MeditationProgram existingProgram = repository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Meditation program not found with ID: " + programId));

        log.info("Existing program found: {}", existingProgram.name());

        // Build program with only non-null fields from request
        MeditationProgram programToUpdate = MeditationProgram.builder()
                .meditationProgramId(programId)
                .name(request.name())
                .description(request.description())
                .nameSi(request.nameSi())
                .descriptionSi(request.descriptionSi())
                .maxSeats(request.maxSeats())
                .isActive(request.isActive())
                .build();

        // Update text fields
        MeditationProgram updatedProgram = repository.updateProgram(programToUpdate);

        // Handle image uploads if R2 is enabled
        if (httpRepository != null) {
            String newCoverImageKey = null;
            Set<String> updatedGalleryImageKeys = null;
            boolean galleryKeysChanged = false;

            // Start with existing gallery keys
            Set<String> currentGalleryKeys = existingProgram.galleryImageKeys() != null
                    ? new HashSet<>(existingProgram.galleryImageKeys())
                    : new HashSet<>();

            // Remove gallery images if requested
            if (request.removeGalleryImageKeys() != null && !request.removeGalleryImageKeys().isEmpty()) {
                log.info("Removing {} gallery images for program {}", request.removeGalleryImageKeys().size(),
                        programId);

                for (String keyToRemove : request.removeGalleryImageKeys()) {
                    if (currentGalleryKeys.contains(keyToRemove)) {
                        log.info("Deleting gallery image from R2: {}", keyToRemove);
                        httpRepository.deleteImage(keyToRemove);
                        currentGalleryKeys.remove(keyToRemove);
                        galleryKeysChanged = true;
                    } else {
                        log.warn("Gallery image key not found in program {}: {}", programId, keyToRemove);
                    }
                }
            }

            // Upload new cover image if provided
            if (coverImage != null && !coverImage.isEmpty()) {
                log.info("Uploading new cover image for program {}", programId);

                // Delete old cover image
                if (existingProgram.coverImageKey() != null) {
                    log.info("Deleting old cover image: {}", existingProgram.coverImageKey());
                    httpRepository.deleteImage(existingProgram.coverImageKey());
                }

                newCoverImageKey = httpRepository.uploadCoverImage(programId, coverImage);
            }

            // Upload new gallery images if provided
            if (galleryImages != null && !galleryImages.isEmpty()) {
                log.info("Uploading {} new gallery images for program {}", galleryImages.size(), programId);

                List<String> uploadedKeys = httpRepository.uploadGalleryImages(programId, galleryImages);
                currentGalleryKeys.addAll(uploadedKeys);
                galleryKeysChanged = true;
            }

            // Set updated gallery keys if changed
            if (galleryKeysChanged) {
                updatedGalleryImageKeys = currentGalleryKeys;
            }

            // Update image keys in database if any were changed
            if (newCoverImageKey != null || updatedGalleryImageKeys != null) {
                log.info("Updating program {} with new image keys", programId);
                updatedProgram = repository.updateImageKeys(programId, newCoverImageKey, updatedGalleryImageKeys);
            }
        } else {
            log.info("R2 is disabled, skipping image uploads");
        }

        log.info("Program updated successfully: {}", updatedProgram.name());

        // Build and return response using presenter
        return responseBuilder.build(updatedProgram);
    }

    /**
     * Update a program without images (backward compatibility for JSON-only
     * requests).
     */
    @Transactional
    public PatchProgramResponse execute(Long programId, PatchProgramRequest request) {
        return execute(programId, request, null, null);
    }
}
