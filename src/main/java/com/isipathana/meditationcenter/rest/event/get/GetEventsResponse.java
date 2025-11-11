package com.isipathana.meditationcenter.rest.event.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Response DTO for GET /api/events endpoint.
 * Contains essential event information for a single event.
 * <p>
 * Image URLs are presigned URLs with 5-minute expiry for secure temporary access.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetEventsResponse(
        @JsonProperty("event_id")
        Long eventId,

        String name,

        String description,

        @JsonProperty("event_date")
        LocalDate eventDate,

        @JsonProperty("start_time")
        LocalTime startTime,

        @JsonProperty("end_time")
        LocalTime endTime,

        String location,

        @JsonProperty("cover_image_url")
        String coverImageUrl,

        @JsonProperty("gallery_image_urls")
        Set<String> galleryImageUrls
) implements ApiResponse {}
