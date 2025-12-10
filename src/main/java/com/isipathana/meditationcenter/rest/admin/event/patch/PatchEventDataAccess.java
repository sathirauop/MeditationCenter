package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.records.event.Event;

import java.util.Optional;

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
}
