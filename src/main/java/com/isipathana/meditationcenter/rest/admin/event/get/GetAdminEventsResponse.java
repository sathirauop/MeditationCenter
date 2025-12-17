package com.isipathana.meditationcenter.rest.admin.event.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Response DTO for GetAdminEvents endpoint.
 * Represents a single event in the admin view.
 * <p>
 * Image URLs are presigned URLs with 5-minute expiry for secure temporary access.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetAdminEventsResponse(
        @JsonProperty("event_id")
        Long eventId,

        String name,

        String description,

        @JsonProperty("name_si")
        String nameSi,

        @JsonProperty("description_si")
        String descriptionSi,

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
) implements ApiResponse {
}
