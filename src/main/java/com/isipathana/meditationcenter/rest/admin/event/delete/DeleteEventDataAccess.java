package com.isipathana.meditationcenter.rest.admin.event.delete;

import com.isipathana.meditationcenter.records.event.Event;

import java.util.Optional;

/**
 * Data access interface for deleting events.
 * <p>
 * Handles database operations for event deletion.
 *
 * @author Sathira Basnayake
 */
public interface DeleteEventDataAccess {

    /**
     * Find an event by its ID.
     * <p>
     * Used to retrieve event details (including image keys) before deletion.
     *
     * @param eventId The ID of the event to find
     * @return Optional containing the event if found, empty otherwise
     */
    Optional<Event> findEventById(Long eventId);

    /**
     * Permanently delete an event from the database (hard delete).
     * <p>
     * This operation cannot be undone. All event data will be removed from the database.
     *
     * @param eventId The ID of the event to delete
     * @return true if event was deleted, false if event was not found
     */
    boolean deleteEvent(Long eventId);
}
