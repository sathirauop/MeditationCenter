package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import com.isipathana.meditationcenter.records.event.Event;

import java.util.Optional;

/**
 * Data access contract for retrieving a single event by ID.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleEventDataAccess {

    /**
     * Find an active event by its ID.
     *
     * @param eventId The ID of the event to find
     * @return Optional containing the event if found and active, empty otherwise
     */
    Optional<Event> findActiveEventById(Long eventId);
}
