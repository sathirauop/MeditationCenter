package com.isipathana.meditationcenter.rest.events.admin.get;

import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.EVENTS;

/**
 * Repository implementation for GetAdminEvents endpoint.
 * Handles database queries for retrieving events (admin view).
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetAdminEventsRepository implements GetAdminEventsDataAccess {

    private final DSLContext dslContext;

    @Override
    public List<Event> findActiveEvents(int offset, int limit) {
        return dslContext
                .selectFrom(EVENTS)
                .where(EVENTS.IS_ACTIVE.eq(true))
                .orderBy(EVENTS.EVENT_DATE.asc(), EVENTS.START_TIME.asc())
                .limit(limit)
                .offset(offset)
                .fetch(record -> Event.builder()
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

    @Override
    public long getActiveEventCount() {
        return dslContext
                .selectCount()
                .from(EVENTS)
                .where(EVENTS.IS_ACTIVE.eq(true))
                .fetchOne(0, long.class);
    }
}
