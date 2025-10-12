package com.isipathana.meditationcenter.rest.admin.event;

import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.EVENTS;

/**
 * Repository implementation for creating events.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostEventRepository implements PostEventDataAccess {

    private final DSLContext dslContext;

    @Override
    public Event createEvent(Event event) {
        var record = dslContext
                .insertInto(EVENTS)
                .set(EVENTS.NAME, event.name())
                .set(EVENTS.DESCRIPTION, event.description())
                .set(EVENTS.EVENT_DATE, event.eventDate())
                .set(EVENTS.START_TIME, event.startTime())
                .set(EVENTS.END_TIME, event.endTime())
                .set(EVENTS.LOCATION, event.location())
                .set(EVENTS.MAX_PARTICIPANTS, event.maxParticipants())
                .set(EVENTS.CURRENT_PARTICIPANTS, 0)
                .set(EVENTS.IMAGES, event.images())
                .set(EVENTS.IS_ACTIVE, event.isActive() != null ? event.isActive() : true)
                .set(EVENTS.REQUIRES_REGISTRATION, event.requiresRegistration() != null ? event.requiresRegistration() : true)
                .returning(
                        EVENTS.EVENT_ID,
                        EVENTS.NAME,
                        EVENTS.DESCRIPTION,
                        EVENTS.EVENT_DATE,
                        EVENTS.START_TIME,
                        EVENTS.END_TIME,
                        EVENTS.LOCATION,
                        EVENTS.MAX_PARTICIPANTS,
                        EVENTS.CURRENT_PARTICIPANTS,
                        EVENTS.IMAGES,
                        EVENTS.IS_ACTIVE,
                        EVENTS.REQUIRES_REGISTRATION,
                        EVENTS.CREATED_AT,
                        EVENTS.UPDATED_AT
                )
                .fetchOne();

        return Event.builder()
                .eventId(record.get(EVENTS.EVENT_ID))
                .name(record.get(EVENTS.NAME))
                .description(record.get(EVENTS.DESCRIPTION))
                .eventDate(record.get(EVENTS.EVENT_DATE))
                .startTime(record.get(EVENTS.START_TIME))
                .endTime(record.get(EVENTS.END_TIME))
                .location(record.get(EVENTS.LOCATION))
                .maxParticipants(record.get(EVENTS.MAX_PARTICIPANTS))
                .currentParticipants(record.get(EVENTS.CURRENT_PARTICIPANTS))
                .images(record.get(EVENTS.IMAGES))
                .isActive(record.get(EVENTS.IS_ACTIVE))
                .requiresRegistration(record.get(EVENTS.REQUIRES_REGISTRATION))
                .createdAt(record.get(EVENTS.CREATED_AT))
                .updatedAt(record.get(EVENTS.UPDATED_AT))
                .build();
    }
}
