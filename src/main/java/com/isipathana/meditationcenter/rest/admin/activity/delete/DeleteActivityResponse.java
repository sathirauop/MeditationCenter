package com.isipathana.meditationcenter.rest.admin.activity.delete;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;

/**
 * Response DTO for activity deletion.
 * Provides confirmation and details about the deleted activity.
 *
 * @author Sathira Basnayake
 */
public record DeleteActivityResponse(
        @JsonProperty("activity_id") Long activityId,
        String message,
        boolean success
) implements ApiResponse {
    
    /**
     * Creates a successful deletion response.
     *
     * @param activityId   The ID of the deleted activity
     * @param activityName The name of the deleted activity
     * @return DeleteActivityResponse with success message
     */
    public static DeleteActivityResponse success(Long activityId, String activityName) {
        String message = String.format("Activity '%s' (ID: %d) has been successfully deleted", activityName, activityId);
        return new DeleteActivityResponse(activityId, message, true);
    }
    
    /**
     * Creates a not found response.
     *
     * @param activityId The ID of the activity that was not found
     * @return DeleteActivityResponse with not found message
     */
    public static DeleteActivityResponse notFound(Long activityId) {
        String message = String.format("Activity with ID %d not found", activityId);
        return new DeleteActivityResponse(activityId, message, false);
    }
}
