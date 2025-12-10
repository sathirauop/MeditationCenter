package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.records.event.Event;

/**
 * Response builder interface for PatchEvent endpoint.
 * Transforms Event domain object to PatchEventResponse DTO.
 *
 * @author Sathira Basnayake
 */
public interface PatchEventResponseBuilder {
    /**
     * Build response from updated event.
     *
     * @param event the updated event
     * @return PatchEventResponse DTO
     */
    PatchEventResponse build(Event event);
}
