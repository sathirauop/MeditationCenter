package com.isipathana.meditationcenter.rest.admin.event.post;

import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Set;

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
                .set(EVENTS.COVER_IMAGE_KEY, event.coverImageKey())
                .set(EVENTS.GALLERY_IMAGE_KEYS, event.galleryImageKeys() != null
                        ? event.galleryImageKeys().toArray(new String[0])
                        : null)
                .set(EVENTS.IS_ACTIVE, event.isActive() != null ? event.isActive() : true)
                .returning(
                        EVENTS.EVENT_ID,
                        EVENTS.NAME,
                        EVENTS.DESCRIPTION,
                        EVENTS.EVENT_DATE,
                        EVENTS.START_TIME,
                        EVENTS.END_TIME,
                        EVENTS.LOCATION,
                        EVENTS.COVER_IMAGE_KEY,
                        EVENTS.GALLERY_IMAGE_KEYS,
                        EVENTS.IS_ACTIVE,
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
                .coverImageKey(record.get(EVENTS.COVER_IMAGE_KEY))
                .galleryImageKeys(record.get(EVENTS.GALLERY_IMAGE_KEYS) != null
                        ? Set.of(record.get(EVENTS.GALLERY_IMAGE_KEYS))
                        : null)
                .isActive(record.get(EVENTS.IS_ACTIVE))
                .createdAt(record.get(EVENTS.CREATED_AT))
                .updatedAt(record.get(EVENTS.UPDATED_AT))
                .build();
    }

    @Override
    public Event updateImageKeys(Long eventId, String coverImageKey, Set<String> galleryImageKeys) {
        var record = dslContext
                .update(EVENTS)
                .set(EVENTS.COVER_IMAGE_KEY, coverImageKey)
                .set(EVENTS.GALLERY_IMAGE_KEYS, galleryImageKeys != null && !galleryImageKeys.isEmpty()
                        ? galleryImageKeys.toArray(new String[0])
                        : null)
                .where(EVENTS.EVENT_ID.eq(eventId))
                .returning(
                        EVENTS.EVENT_ID,
                        EVENTS.NAME,
                        EVENTS.DESCRIPTION,
                        EVENTS.EVENT_DATE,
                        EVENTS.START_TIME,
                        EVENTS.END_TIME,
                        EVENTS.LOCATION,
                        EVENTS.COVER_IMAGE_KEY,
                        EVENTS.GALLERY_IMAGE_KEYS,
                        EVENTS.IS_ACTIVE,
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
                .coverImageKey(record.get(EVENTS.COVER_IMAGE_KEY))
                .galleryImageKeys(record.get(EVENTS.GALLERY_IMAGE_KEYS) != null
                        ? Set.of(record.get(EVENTS.GALLERY_IMAGE_KEYS))
                        : null)
                .isActive(record.get(EVENTS.IS_ACTIVE))
                .createdAt(record.get(EVENTS.CREATED_AT))
                .updatedAt(record.get(EVENTS.UPDATED_AT))
                .build();
    }
}
