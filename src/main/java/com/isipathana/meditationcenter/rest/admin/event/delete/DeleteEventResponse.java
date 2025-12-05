package com.isipathana.meditationcenter.rest.admin.event.delete;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for event deletion.
 * <p>
 * Provides confirmation and details about the deleted event.
 *
 * @author Sathira Basnayake
 */
public record DeleteEventResponse(
        @JsonProperty("event_id") Long eventId,
        String message,
        @JsonProperty("images_deleted") boolean imagesDeleted
) {
    /**
     * Creates a successful deletion response.
     *
     * @param eventId       The ID of the deleted event
     * @param eventName     The name of the deleted event
     * @param imagesDeleted Whether images were deleted from R2
     * @return DeleteEventResponse with success message
     */
    public static DeleteEventResponse success(Long eventId, String eventName, boolean imagesDeleted) {
        String message = String.format("Event '%s' (ID: %d) has been successfully deleted", eventName, eventId);
        return new DeleteEventResponse(eventId, message, imagesDeleted);
    }

    /**
     * Creates a not found response.
     *
     * @param eventId The ID of the event that was not found
     * @return DeleteEventResponse with not found message
     */
    public static DeleteEventResponse notFound(Long eventId) {
        String message = String.format("Event with ID %d not found", eventId);
        return new DeleteEventResponse(eventId, message, false);
    }
}
