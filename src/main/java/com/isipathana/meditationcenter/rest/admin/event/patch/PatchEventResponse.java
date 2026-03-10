package com.isipathana.meditationcenter.rest.admin.event.patch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Response DTO for event update.
 * Returns presigned URLs for images (not raw keys) for frontend display.
 *
 * @author Sathira Basnayake
 */
public record PatchEventResponse(
                @JsonProperty("event_id") Long eventId,

                String name,

                String description,

                @JsonProperty("name_si") String nameSi,

                @JsonProperty("description_si") String descriptionSi,

                @JsonProperty("event_date") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate eventDate,

                @JsonProperty("start_time") @JsonFormat(pattern = "HH:mm") LocalTime startTime,

                @JsonProperty("end_time") @JsonFormat(pattern = "HH:mm") LocalTime endTime,

                String location,

                @JsonProperty("cover_image_url") String coverImageUrl,

                @JsonProperty("gallery_image_urls") Set<String> galleryImageUrls,

                @JsonProperty("is_active") Boolean isActive,

                @JsonProperty("updated_at") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt) {
}
