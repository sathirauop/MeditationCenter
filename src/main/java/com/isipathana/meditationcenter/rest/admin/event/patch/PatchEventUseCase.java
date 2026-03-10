package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.cache.EventImageUrlsCache;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.event.Event;
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
 * UseCase for updating an event (partial update).
 * Handles business logic for PATCH /api/admin/event/{eventId}.
 * <p>
 * Supports optional image updates (cover image and gallery images).
 * When new images are uploaded, old images are deleted from R2 and cache is
 * invalidated.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatchEventUseCase implements UseCase<PatchEventRequest, PatchEventResponse> {

        private final PatchEventDataAccess repository;
        private final PatchEventResponseBuilder presenter;
        private final EventImageUrlsCache imageUrlsCache;

        // Optional - only injected when R2 is enabled
        @Autowired(required = false)
        private PatchEventHttpDataAccess httpRepository;

        /**
         * Update event with partial data and optional image updates.
         * Only non-null fields in the request will be updated.
         * <p>
         * Image update flow:
         * 1. Upload new image(s) to R2
         * 2. Delete old image(s) from R2
         * 3. Invalidate cached presigned URLs for old images
         * 4. Update image keys in database
         *
         * @param eventId       the ID of the event to update
         * @param request       the update request with optional fields
         * @param coverImage    optional new cover image (null to keep existing)
         * @param galleryImages optional new gallery images (null to keep existing,
         *                      empty list to clear)
         * @return the updated event
         */
        @Transactional
        public PatchEventResponse execute(Long eventId, PatchEventRequest request,
                        MultipartFile coverImage, List<MultipartFile> galleryImages) {
                log.info("Updating event ID: {}", eventId);

                // Verify event exists and get current state
                Event existingEvent = repository.findEventById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Event not found with ID: " + eventId));

                log.debug("Found existing event: {}", existingEvent.name());

                // Step 1: Update text fields if any are provided in the request
                if (request != null) {
                        Event eventToUpdate = Event.builder()
                                        .eventId(eventId)
                                        .name(request.name())
                                        .description(request.description())
                                        .nameSi(request.nameSi())
                                        .descriptionSi(request.descriptionSi())
                                        .eventDate(request.eventDate())
                                        .startTime(request.startTime())
                                        .endTime(request.endTime())
                                        .location(request.location())
                                        .isActive(request.isActive())
                                        .build();

                        repository.updateEvent(eventToUpdate);
                }

                // Step 2: Handle image updates if R2 is enabled
                if (httpRepository != null) {
                        handleImageUpdates(eventId, existingEvent, coverImage, galleryImages);
                }

                // Fetch the final state and build response
                Event updatedEvent = repository.findEventById(eventId)
                                .orElseThrow(() -> new IllegalStateException("Event not found after update"));

                log.info("Successfully updated event ID: {}", eventId);

                return presenter.build(updatedEvent);
        }

        /**
         * Handle image uploads, deletions, and database updates.
         */
        private void handleImageUpdates(Long eventId, Event existingEvent,
                        MultipartFile coverImage, List<MultipartFile> galleryImages) {
                String newCoverImageKey = null;
                Set<String> newGalleryImageKeys = null;
                boolean imageKeysChanged = false;

                // Handle cover image update
                if (coverImage != null && !coverImage.isEmpty()) {
                        log.info("Updating cover image for event {}", eventId);

                        // Upload new cover image
                        newCoverImageKey = httpRepository.uploadCoverImage(eventId, coverImage);

                        // Delete old cover image from R2
                        if (existingEvent.coverImageKey() != null && !existingEvent.coverImageKey().isEmpty()) {
                                httpRepository.deleteImage(existingEvent.coverImageKey());
                                imageUrlsCache.invalidate(existingEvent.coverImageKey());
                        }

                        imageKeysChanged = true;
                }

                // Handle gallery images update
                if (galleryImages != null) {
                        log.info("Updating gallery images for event {}", eventId);

                        if (!galleryImages.isEmpty()) {
                                // Upload new gallery images
                                List<String> uploadedKeys = httpRepository.uploadGalleryImages(eventId, galleryImages);

                                // Merge with existing gallery keys (add new ones)
                                newGalleryImageKeys = new HashSet<>();
                                if (existingEvent.galleryImageKeys() != null) {
                                        newGalleryImageKeys.addAll(existingEvent.galleryImageKeys());
                                }
                                newGalleryImageKeys.addAll(uploadedKeys);
                        }

                        imageKeysChanged = true;
                }

                // Update image keys in database if anything changed
                if (imageKeysChanged) {
                        repository.updateImageKeys(eventId, newCoverImageKey, newGalleryImageKeys);
                }
        }

        /**
         * Update event with partial data only (no image changes).
         * Backward compatible method.
         */
        @Transactional
        public PatchEventResponse execute(Long eventId, PatchEventRequest request) {
                return execute(eventId, request, null, null);
        }

        @Override
        public PatchEventResponse handle(PatchEventRequest request) {
                throw new UnsupportedOperationException(
                                "Use execute(Long eventId, PatchEventRequest request) instead");
        }
}
