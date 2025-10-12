package com.isipathana.meditationcenter.rest.admin.event;

import com.isipathana.meditationcenter.records.event.Event;

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
}
