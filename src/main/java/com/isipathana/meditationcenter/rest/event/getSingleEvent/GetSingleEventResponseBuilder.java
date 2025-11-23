package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import com.isipathana.meditationcenter.records.event.Event;

/**
 * Builder interface for creating GetSingleEvent response.
 * Transforms an Event domain object into a GetSingleEventResponse.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleEventResponseBuilder {

    /**
     * Build response from Event domain object.
     * Generates presigned URLs for images if R2 is enabled.
     *
     * @param event Event domain object
     * @return GetSingleEventResponse with event details and presigned image URLs
     */
    GetSingleEventResponse build(Event event);
}
