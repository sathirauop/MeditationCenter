package com.isipathana.meditationcenter.rest.events.get;

import com.isipathana.meditationcenter.records.event.Event;
import com.isipathana.meditationcenter.rest.event.get.GetEventsRepository;
import com.isipathana.meditationcenter.seeds.EventSeed;
import com.isipathana.meditationcenter.testspec.SpringPostgreSQLIntegrationSpec;
import com.isipathana.meditationcenter.wrappers.TestDSLContextWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.isipathana.meditationcenter.jooq.Tables.EVENTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository test for GetEvents endpoint.
 * Tests database queries using real PostgreSQL testcontainer.
 *
 * @author Sathira Basnayake
 */
public class GetEventsRepositoryTest extends SpringPostgreSQLIntegrationSpec {

    @Autowired
    private GetEventsRepository repository;

    @Autowired
    private TestDSLContextWrapper dslContextWrapper;

    @BeforeEach
    void setUp() {
        dslContextWrapper.seedData(EVENTS, EventSeed.seed());
    }

    @Test
    void shouldReturnActiveEventsOrdered_whenEventsExist() {
        List<Event> events = repository.findActiveEvents(0, 10);

        assertNotNull(events);
        assertEquals(4, events.size()); // 4 active events (event4 is inactive)

        // Verify ordering by event_date ASC, start_time ASC
        assertEquals("Wesak Celebration", events.get(0).name());
        assertEquals(LocalDate.of(2024, 5, 15), events.get(0).eventDate());

        assertEquals("Meditation Retreat", events.get(1).name());
        assertEquals(LocalDate.of(2024, 6, 10), events.get(1).eventDate());

        assertEquals("Dhamma Talk", events.get(2).name());
        assertEquals(LocalDate.of(2024, 7, 5), events.get(2).eventDate());

        assertEquals("New Year Blessing", events.get(3).name());
        assertEquals(LocalDate.of(2025, 1, 1), events.get(3).eventDate());
    }

    @Test
    void shouldReturnEmptyList_whenNoActiveEvents() {
        // Purge all data
        dslContextWrapper.purgeData(EVENTS);

        List<Event> events = repository.findActiveEvents(0, 10);

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    void shouldRespectLimitAndOffset() {
        // Get first 2 events
        List<Event> page1 = repository.findActiveEvents(0, 2);
        assertEquals(2, page1.size());
        assertEquals("Wesak Celebration", page1.get(0).name());
        assertEquals("Meditation Retreat", page1.get(1).name());

        // Get next 2 events (offset=2)
        List<Event> page2 = repository.findActiveEvents(2, 2);
        assertEquals(2, page2.size());
        assertEquals("Dhamma Talk", page2.get(0).name());
        assertEquals("New Year Blessing", page2.get(1).name());

        // Get beyond available data
        List<Event> page3 = repository.findActiveEvents(10, 2);
        assertTrue(page3.isEmpty());
    }

    @Test
    void shouldOrderByEventDateAndStartTime() {
        List<Event> events = repository.findActiveEvents(0, 10);

        // Events should be ordered by event_date ASC, then start_time ASC
        for (int i = 0; i < events.size() - 1; i++) {
            Event current = events.get(i);
            Event next = events.get(i + 1);

            // Either current event_date is before next
            // OR same date but current start_time is before next
            assertTrue(
                    current.eventDate().isBefore(next.eventDate()) ||
                            (current.eventDate().isEqual(next.eventDate()) &&
                                    !current.startTime().isAfter(next.startTime()))
            );
        }
    }

    @Test
    void shouldReturnCorrectCount_whenCountingActiveEvents() {
        long count = repository.getActiveEventCount();

        assertEquals(4L, count); // 4 active events (excluding inactive event4)
    }

    @Test
    void shouldExcludeInactiveEvents() {
        List<Event> events = repository.findActiveEvents(0, 10);

        // Verify no inactive events are returned
        boolean hasInactiveEvent = events.stream()
                .anyMatch(event -> event.name().equals("Cancelled Workshop"));

        assertFalse(hasInactiveEvent, "Inactive events should not be returned");

        // Verify all returned events are active
        events.forEach(event ->
                assertTrue(event.isActive(), "All returned events should be active")
        );
    }

    @Test
    void shouldReturnAllEventFields() {
        List<Event> events = repository.findActiveEvents(0, 1);

        assertFalse(events.isEmpty());
        Event event = events.get(0);

        // Verify all fields are populated
        assertNotNull(event.eventId());
        assertNotNull(event.name());
        assertNotNull(event.description());
        assertNotNull(event.eventDate());
        assertNotNull(event.startTime());
        assertNotNull(event.endTime());
        assertNotNull(event.location());
        // images can be null or empty string
        assertNotNull(event.isActive());
        assertNotNull(event.createdAt());
        assertNotNull(event.updatedAt());
    }

    @Test
    void shouldRespectLimit1() {
        List<Event> events = repository.findActiveEvents(0, 1);

        assertEquals(1, events.size());
    }

    @Test
    void shouldHandleZeroLimit() {
        List<Event> events = repository.findActiveEvents(0, 0);

        assertTrue(events.isEmpty());
    }
}
