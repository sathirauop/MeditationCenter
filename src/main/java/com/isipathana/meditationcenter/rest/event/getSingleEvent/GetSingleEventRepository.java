package com.isipathana.meditationcenter.rest.event.getSingleEvent;

import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.EVENTS;

/**
 * Repository implementation for retrieving a single event using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetSingleEventRepository implements GetSingleEventDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<Event> findActiveEventById(Long eventId) {
        return dslContext
                .selectFrom(EVENTS)
                .where(EVENTS.EVENT_ID.eq(eventId))
                .and(EVENTS.IS_ACTIVE.eq(true))
                .fetchOptional(record -> Event.builder()
                        .eventId(record.get(EVENTS.EVENT_ID))
                        .name(record.get(EVENTS.NAME))
                        .description(record.get(EVENTS.DESCRIPTION))
                        .eventDate(record.get(EVENTS.EVENT_DATE))
                        .startTime(record.get(EVENTS.START_TIME))
                        .endTime(record.get(EVENTS.END_TIME))
                        .location(record.get(EVENTS.LOCATION))
                        .coverImageKey(record.get(EVENTS.COVER_IMAGE_KEY))
                        .galleryImageKeys(record.get(EVENTS.GALLERY_IMAGE_KEYS) != null
                                ? Set.of(record.get(EVENTS.GALLERY_IMAGE_KEYS))
                                : null)
                        .isActive(record.get(EVENTS.IS_ACTIVE))
                        .createdAt(record.get(EVENTS.CREATED_AT))
                        .updatedAt(record.get(EVENTS.UPDATED_AT))
                        .build()
                );
    }
}
