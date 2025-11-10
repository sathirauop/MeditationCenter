package com.isipathana.meditationcenter.seeds;

import com.isipathana.meditationcenter.jooq.tables.records.EventsRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.isipathana.meditationcenter.jooq.Tables.EVENTS;

/**
 * Seed data for events table in tests.
 *
 * @author Sathira Basnayake
 */
public class EventSeed {

    /**
     * Provides test event data.
     * Returns 5 events with mix of active/inactive and different dates.
     */
    public static List<EventsRecord> seed() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 5, 21, 8, 30, 0);

        EventsRecord event1 = EVENTS.newRecord();
        event1.setEventId(1L);
        event1.setName("Wesak Celebration");
        event1.setDescription("Celebrate the birth, enlightenment, and passing of Buddha");
        event1.setEventDate(LocalDate.of(2024, 5, 15));
        event1.setStartTime(LocalTime.of(6, 0));
        event1.setEndTime(LocalTime.of(20, 0));
        event1.setLocation("Main Hall");
        event1.setImages("/images/wesak1.jpg,/images/wesak2.jpg");
        event1.setIsActive(true);
        event1.setCreatedAt(fixedTime);
        event1.setUpdatedAt(fixedTime);

        EventsRecord event2 = EVENTS.newRecord();
        event2.setEventId(2L);
        event2.setName("Meditation Retreat");
        event2.setDescription("3-day intensive meditation retreat for practitioners");
        event2.setEventDate(LocalDate.of(2024, 6, 10));
        event2.setStartTime(LocalTime.of(9, 0));
        event2.setEndTime(LocalTime.of(17, 0));
        event2.setLocation("Retreat Center");
        event2.setImages("/images/retreat1.jpg");
        event2.setIsActive(true);
        event2.setCreatedAt(fixedTime);
        event2.setUpdatedAt(fixedTime);

        EventsRecord event3 = EVENTS.newRecord();
        event3.setEventId(3L);
        event3.setName("Dhamma Talk");
        event3.setDescription("Weekly dhamma discussion and Q&A session");
        event3.setEventDate(LocalDate.of(2024, 7, 5));
        event3.setStartTime(LocalTime.of(18, 30));
        event3.setEndTime(LocalTime.of(20, 30));
        event3.setLocation("Conference Room");
        event3.setImages("");
        event3.setIsActive(true);
        event3.setCreatedAt(fixedTime);
        event3.setUpdatedAt(fixedTime);

        // Inactive event - should not appear in active queries
        EventsRecord event4 = EVENTS.newRecord();
        event4.setEventId(4L);
        event4.setName("Cancelled Workshop");
        event4.setDescription("This event has been cancelled");
        event4.setEventDate(LocalDate.of(2024, 8, 20));
        event4.setStartTime(LocalTime.of(10, 0));
        event4.setEndTime(LocalTime.of(12, 0));
        event4.setLocation("Workshop Hall");
        event4.setImages("");
        event4.setIsActive(false);
        event4.setCreatedAt(fixedTime);
        event4.setUpdatedAt(fixedTime);

        EventsRecord event5 = EVENTS.newRecord();
        event5.setEventId(5L);
        event5.setName("New Year Blessing");
        event5.setDescription("Traditional new year blessing ceremony");
        event5.setEventDate(LocalDate.of(2025, 1, 1));
        event5.setStartTime(LocalTime.of(7, 0));
        event5.setEndTime(LocalTime.of(9, 0));
        event5.setLocation("Main Hall");
        event5.setImages("/images/newyear1.jpg,/images/newyear2.jpg,/images/newyear3.jpg");
        event5.setIsActive(true);
        event5.setCreatedAt(fixedTime);
        event5.setUpdatedAt(fixedTime);

        return List.of(event1, event2, event3, event4, event5);
    }
}
