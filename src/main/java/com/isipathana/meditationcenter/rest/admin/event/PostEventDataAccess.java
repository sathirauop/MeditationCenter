package com.isipathana.meditationcenter.rest.admin.event;

import com.isipathana.meditationcenter.records.event.Event;

import java.util.Set;

/**
 * Data access interface for creating events.
 *
 * @author Sathira Basnayake
 */
public interface PostEventDataAccess {

    /**
     * Create a new event.
     *
     * @param event Event to create
     * @return Created event with generated ID
     */
    Event createEvent(Event event);

    /**
     * Update image keys for an existing event.
     * <p>
     * Used after uploading images to R2 to save the keys to the database.
     *
     * @param eventId         The ID of the event to update
     * @param coverImageKey   The R2 key for the cover image (can be null)
     * @param galleryImageKeys The R2 keys for gallery images (can be null or empty)
     * @return Updated event with image keys
     */
    Event updateImageKeys(Long eventId, String coverImageKey, Set<String> galleryImageKeys);
}
