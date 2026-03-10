package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.records.event.Event;

import java.util.Optional;
import java.util.Set;

/**
 * Data access contract for updating events.
 *
 * @author Sathira Basnayake
 */
public interface PatchEventDataAccess {

    /**
     * Find event by ID.
     *
     * @param eventId the event ID
     * @return Optional containing the event if found
     */
    Optional<Event> findEventById(Long eventId);

    /**
     * Update event with partial data.
     * Only non-null fields in the event object will be updated.
     *
     * @param event the event with updated fields
     * @return the updated event
     */
    Event updateEvent(Event event);

    /**
     * Update image keys for an existing event.
     * <p>
     * Used after uploading replacement images to R2 to save the new keys to the
     * database.
     *
     * @param eventId          The ID of the event to update
     * @param coverImageKey    The new R2 key for the cover image (null to keep
     *                         existing)
     * @param galleryImageKeys The new R2 keys for gallery images (null to keep
     *                         existing)
     * @return Updated event with new image keys
     */
    Event updateImageKeys(Long eventId, String coverImageKey, Set<String> galleryImageKeys);
}
