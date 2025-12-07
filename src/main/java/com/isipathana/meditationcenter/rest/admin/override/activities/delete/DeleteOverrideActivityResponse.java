package com.isipathana.meditationcenter.rest.admin.override.activities.delete;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for deleting activity from override.
 *
 * @author Sathira Basnayake
 */
public record DeleteOverrideActivityResponse(
        boolean success,

        String message,

        @JsonProperty("override_activity_id")
        Long overrideActivityId,

        @JsonProperty("override_id")
        Long overrideId,

        @JsonProperty("activity_title")
        String activityTitle
) {}
