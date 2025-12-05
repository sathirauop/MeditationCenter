package com.isipathana.meditationcenter.rest.admin.event.get;

import com.isipathana.meditationcenter.records.event.Event;

import java.util.List;

/**
 * Data access interface for GetAdminEvents endpoint.
 * Defines contract for retrieving events with pagination (admin view).
 *
 * @author Sathira Basnayake
 */
public interface GetAdminEventsDataAccess {
    /**
     * Find active events with pagination.
     *
     * @param offset the offset to start from
     * @param limit  the maximum number of events to return
     * @return list of events
     */
    List<Event> findActiveEvents(int offset, int limit);

    /**
     * Get the total count of active events.
     *
     * @return total count of active events
     */
    long getActiveEventCount();
}
