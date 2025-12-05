package com.isipathana.meditationcenter.rest.admin.event.delete;

import com.isipathana.meditationcenter.cache.EventImageUrlsCache;
import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * UseCase for deleting an event (hard delete).
 * <p>
 * Handles business logic for event deletion by admin users.
 * Performs hard delete - permanently removes the event and associated images from R2.
 * <p>
 * Deletion Flow:
 * 1. Fetch event from database (to get image keys)
 * 2. Delete images from R2 storage (if R2 enabled)
 * 3. Invalidate cached presigned URLs
 * 4. Delete event from database
 * <p>
 * IMPORTANT: This is a hard delete - the operation is irreversible.
 * Consider using soft delete (is_active flag) if you need audit trails or restore capability.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
public class DeleteEventUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DeleteEventUseCase.class);

    private final DeleteEventDataAccess repository;
    private final EventImageUrlsCache imageUrlsCache;

    // Optional - only injected when R2 is enabled
    @Autowired(required = false)
    private DeleteEventHttpDataAccess httpRepository;

    /**
     * Execute event deletion.
     * <p>
     * Performs hard delete - permanently removes event and associated images.
     * Transaction ensures database consistency - rollback if any step fails.
     *
     * @param eventId The ID of the event to delete
     * @return DeleteEventResponse with deletion status
     */
    @Transactional
    public DeleteEventResponse execute(Long eventId) {
        logger.info("Starting deletion process for event ID: {}", eventId);

        // Step 1: Fetch event to get image keys and verify existence
        Optional<Event> eventOptional = repository.findEventById(eventId);

        if (eventOptional.isEmpty()) {
            logger.warn("Event not found for deletion: {}", eventId);
            return DeleteEventResponse.notFound(eventId);
        }

        Event event = eventOptional.get();
        String eventName = event.name();
        boolean imagesDeleted = false;

        // Step 2: Delete images from R2 if R2 is enabled
        if (httpRepository != null) {
            logger.info("R2 enabled - attempting to delete images for event {}", eventId);

            // Use folder deletion for efficiency - removes entire events/{eventId}/ folder
            boolean r2Success = httpRepository.deleteEventFolder(eventId);

            if (r2Success) {
                logger.info("Successfully deleted images from R2 for event {}", eventId);
                imagesDeleted = true;

                // Step 3: Invalidate cached presigned URLs
                invalidateCache(event);
            } else {
                logger.error("Failed to delete images from R2 for event {}", eventId);
                // Note: We continue with database deletion even if R2 deletion fails
                // This prevents orphaned database records if R2 is unavailable
                // Orphaned R2 files can be cleaned up later via maintenance script
            }
        } else {
            logger.debug("R2 not enabled - skipping image deletion for event {}", eventId);
        }

        // Step 4: Delete event from database (hard delete)
        boolean dbSuccess = repository.deleteEvent(eventId);

        if (!dbSuccess) {
            // This should never happen since we verified existence, but handle it gracefully
            logger.error("Failed to delete event {} from database despite existence check", eventId);
            throw new RuntimeException("Failed to delete event from database");
        }

        logger.info("Successfully deleted event {} from database", eventId);

        return DeleteEventResponse.success(eventId, eventName, imagesDeleted);
    }

    /**
     * Invalidates cached presigned URLs for event images.
     * <p>
     * Called after successful R2 deletion to ensure stale URLs aren't served.
     *
     * @param event The event whose image URLs should be invalidated
     */
    private void invalidateCache(Event event) {
        // Invalidate cover image cache
        if (event.coverImageKey() != null && !event.coverImageKey().isEmpty()) {
            logger.debug("Invalidating cache for cover image: {}", event.coverImageKey());
            imageUrlsCache.invalidate(event.coverImageKey());
        }

        // Invalidate gallery image caches
        if (event.galleryImageKeys() != null && !event.galleryImageKeys().isEmpty()) {
            for (String galleryKey : event.galleryImageKeys()) {
                logger.debug("Invalidating cache for gallery image: {}", galleryKey);
                imageUrlsCache.invalidate(galleryKey);
            }
        }

        logger.info("Invalidated {} cached image URLs for event {}",
                (event.coverImageKey() != null ? 1 : 0) +
                        (event.galleryImageKeys() != null ? event.galleryImageKeys().size() : 0),
                event.eventId());
    }
}
