package com.isipathana.meditationcenter.rest.admin.event.delete;

import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

import static com.isipathana.meditationcenter.jooq.Tables.EVENTS;

/**
 * Repository implementation for deleting events.
 * <p>
 * Handles database operations for event deletion using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class DeleteEventRepository implements DeleteEventDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<Event> findEventById(Long eventId) {
        var record = dslContext
                .selectFrom(EVENTS)
                .where(EVENTS.EVENT_ID.eq(eventId))
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        Event event = Event.builder()
                .eventId(record.get(EVENTS.EVENT_ID))
                .name(record.get(EVENTS.NAME))
                .description(record.get(EVENTS.DESCRIPTION))
                .nameSi(record.get(EVENTS.NAME_SI))
                .descriptionSi(record.get(EVENTS.DESCRIPTION_SI))
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
                .build();

        return Optional.of(event);
    }

    @Override
    public boolean deleteEvent(Long eventId) {
        int deletedRows = dslContext
                .deleteFrom(EVENTS)
                .where(EVENTS.EVENT_ID.eq(eventId))
                .execute();

        return deletedRows > 0;
    }
}
