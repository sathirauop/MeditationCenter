package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.records.event.Event;
import org.springframework.stereotype.Component;

/**
 * Presenter for PatchEvent endpoint.
 * Transforms Event domain object to PatchEventResponse DTO.
 *
 * @author Sathira Basnayake
 */
@Component
public class PatchEventPresenter implements PatchEventResponseBuilder {

    @Override
    public PatchEventResponse build(Event event) {
        return new PatchEventResponse(
                event.eventId(),
                event.name(),
                event.description(),
                event.eventDate(),
                event.startTime(),
                event.endTime(),
                event.location(),
                event.coverImageKey(),
                event.galleryImageKeys(),
                event.isActive(),
                event.updatedAt()
        );
    }
}
