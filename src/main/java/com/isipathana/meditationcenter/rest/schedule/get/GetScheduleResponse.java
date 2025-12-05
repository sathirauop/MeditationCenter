package com.isipathana.meditationcenter.rest.schedule.get;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for daily schedule.
 * Returns either override schedule or template schedule.
 *
 * @author Sathira Basnayake
 */
public record GetScheduleResponse(
        @JsonProperty("schedule_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate scheduleDate,

        @JsonProperty("schedule_type")
        ScheduleType scheduleType,

        @JsonProperty("schedule_name")
        String scheduleName,

        List<ScheduleActivity> activities
) {
    public enum ScheduleType {
        OVERRIDE,
        TEMPLATE,
        NONE
    }

    public record ScheduleActivity(
            @JsonProperty("activity_id")
            Long activityId,

            @JsonProperty("activity_title")
            String activityTitle,

            @JsonProperty("activity_description")
            String activityDescription,

            @JsonProperty("start_time")
            @JsonFormat(pattern = "HH:mm")
            String startTime,

            @JsonProperty("end_time")
            @JsonFormat(pattern = "HH:mm")
            String endTime,

            String notes
    ) {}
}
