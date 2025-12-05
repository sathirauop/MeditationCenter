package com.isipathana.meditationcenter.rest.event.get;

import com.isipathana.meditationcenter.records.event.Event;

import java.util.List;

/**
 * Data access contract for retrieving events.
 *
 * @author Sathira Basnayake
 */
public interface GetEventsDataAccess {

    /**
     * Find active events with pagination support.
     * Results are ordered by event date and start time.
     *
     * @param offset Starting position (0-based)
     * @param limit  Maximum number of results
     * @return List of active events
     */
    List<Event> findActiveEvents(int offset, int limit);

    /**
     * Get total count of active events.
     *
     * @return Total count of active events
     */
    long getActiveEventCount();
}
