package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.isipathana.meditationcenter.records.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.isipathana.meditationcenter.jooq.tables.Events.EVENTS;

/**
 * Repository implementation for updating events.
 * Uses jOOQ for type-safe database access with partial updates.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PatchEventRepository implements PatchEventDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<Event> findEventById(Long eventId) {
        log.debug("Finding event by ID: {}", eventId);

        var record = dslContext
                .selectFrom(EVENTS)
                .where(EVENTS.EVENT_ID.eq(eventId))
                .fetchOne();

        if (record == null) {
            log.debug("Event not found with ID: {}", eventId);
            return Optional.empty();
        }

        Set<String> galleryImageKeys = record.getGalleryImageKeys() != null
                ? Arrays.stream(record.getGalleryImageKeys()).collect(Collectors.toSet())
                : null;

        Event event = Event.builder()
                .eventId(record.getEventId())
                .name(record.getName())
                .description(record.getDescription())
                .nameSi(record.getNameSi())
                .descriptionSi(record.getDescriptionSi())
                .eventDate(record.getEventDate())
                .startTime(record.getStartTime())
                .endTime(record.getEndTime())
                .location(record.getLocation())
                .coverImageKey(record.getCoverImageKey())
                .galleryImageKeys(galleryImageKeys)
                .isActive(record.getIsActive())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();

        log.debug("Found event: {}", event.name());
        return Optional.of(event);
    }

    @Override
    public Event updateEvent(Event event) {
        log.info("Updating event ID: {}", event.eventId());

        // Build dynamic update query - only update non-null fields
        var updateStep = dslContext.update(EVENTS);
        UpdateSetMoreStep<?> query = null;

        if (event.name() != null) {
            query = updateStep.set(EVENTS.NAME, event.name());
        }

        if (event.description() != null) {
            query = query != null
                    ? query.set(EVENTS.DESCRIPTION, event.description())
                    : updateStep.set(EVENTS.DESCRIPTION, event.description());
        }

        if (event.nameSi() != null) {
            query = query != null
                    ? query.set(EVENTS.NAME_SI, event.nameSi())
                    : updateStep.set(EVENTS.NAME_SI, event.nameSi());
        }

        if (event.descriptionSi() != null) {
            query = query != null
                    ? query.set(EVENTS.DESCRIPTION_SI, event.descriptionSi())
                    : updateStep.set(EVENTS.DESCRIPTION_SI, event.descriptionSi());
        }

        if (event.eventDate() != null) {
            query = query != null
                    ? query.set(EVENTS.EVENT_DATE, event.eventDate())
                    : updateStep.set(EVENTS.EVENT_DATE, event.eventDate());
        }

        if (event.startTime() != null) {
            query = query != null
                    ? query.set(EVENTS.START_TIME, event.startTime())
                    : updateStep.set(EVENTS.START_TIME, event.startTime());
        }

        if (event.endTime() != null) {
            query = query != null
                    ? query.set(EVENTS.END_TIME, event.endTime())
                    : updateStep.set(EVENTS.END_TIME, event.endTime());
        }

        if (event.location() != null) {
            query = query != null
                    ? query.set(EVENTS.LOCATION, event.location())
                    : updateStep.set(EVENTS.LOCATION, event.location());
        }

        if (event.isActive() != null) {
            query = query != null
                    ? query.set(EVENTS.IS_ACTIVE, event.isActive())
                    : updateStep.set(EVENTS.IS_ACTIVE, event.isActive());
        }

        // Always update updated_at timestamp
        if (query != null) {
            query = query.set(EVENTS.UPDATED_AT, org.jooq.impl.DSL.currentLocalDateTime());
        }

        // Execute update if any fields were set
        if (query != null) {
            query.where(EVENTS.EVENT_ID.eq(event.eventId())).execute();
            log.info("Successfully updated event ID: {}", event.eventId());
        } else {
            log.warn("No fields to update for event ID: {}", event.eventId());
        }

        // Fetch and return updated event
        return findEventById(event.eventId())
                .orElseThrow(() -> new IllegalStateException("Event not found after update"));
    }

    @Override
    public Event updateImageKeys(Long eventId, String coverImageKey, Set<String> galleryImageKeys) {
        log.info("Updating image keys for event ID: {}", eventId);

        var updateStep = dslContext.update(EVENTS);
        UpdateSetMoreStep<?> query = null;

        if (coverImageKey != null) {
            query = updateStep.set(EVENTS.COVER_IMAGE_KEY, coverImageKey);
        }

        if (galleryImageKeys != null) {
            if (query != null) {
                query = query.set(EVENTS.GALLERY_IMAGE_KEYS,
                        galleryImageKeys.isEmpty() ? null : galleryImageKeys.toArray(new String[0]));
            } else {
                query = updateStep.set(EVENTS.GALLERY_IMAGE_KEYS,
                        galleryImageKeys.isEmpty() ? null : galleryImageKeys.toArray(new String[0]));
            }
        }

        if (query != null) {
            query = query.set(EVENTS.UPDATED_AT, org.jooq.impl.DSL.currentLocalDateTime());
            query.where(EVENTS.EVENT_ID.eq(eventId)).execute();
            log.info("Successfully updated image keys for event ID: {}", eventId);
        }

        return findEventById(eventId)
                .orElseThrow(() -> new IllegalStateException("Event not found after image key update"));
    }
}
