package com.isipathana.meditationcenter.rest.admin.override.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;

import java.util.List;
import java.util.Optional;

/**
 * Data access interface for creating schedule overrides.
 *
 * @author Sathira Basnayake
 */
public interface PostOverrideDataAccess {
    /**
     * Find activity by ID.
     */
    Optional<Activity> findActivityById(Long activityId);

    /**
     * Check if override already exists for the given date.
     */
    boolean overrideExistsForDate(java.time.LocalDate date);

    /**
     * Create a new schedule override.
     */
    ScheduleOverride createOverride(ScheduleOverride override);

    /**
     * Create an override activity.
     */
    OverrideActivity createOverrideActivity(OverrideActivity overrideActivity);

    /**
     * Find activities by IDs.
     */
    List<Activity> findActivitiesByIds(List<Long> activityIds);

    /**
     * Delete override and all its activities for the given date.
     */
    void deleteOverrideByDate(java.time.LocalDate date);
}
