package com.isipathana.meditationcenter.rest.admin.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response DTO for event creation.
 *
 * @author Sathira Basnayake
 */
public record PostEventResponse(
        @JsonProperty("event_id")
        Long eventId,

        String name,

        String description,

        @JsonProperty("event_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate eventDate,

        @JsonProperty("start_time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @JsonProperty("end_time")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        String location,

        @JsonProperty("max_participants")
        Integer maxParticipants,

        @JsonProperty("current_participants")
        Integer currentParticipants,

        String images,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("requires_registration")
        Boolean requiresRegistration,

        @JsonProperty("created_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
}
