package com.isipathana.meditationcenter.rest.admin.override.activities.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;

import java.util.Optional;

/**
 * Data access interface for adding activities to overrides.
 *
 * @author Sathira Basnayake
 */
public interface PostOverrideActivityDataAccess {
    Optional<ScheduleOverride> findOverrideById(Long overrideId);
    Optional<Activity> findActivityById(Long activityId);
    OverrideActivity createOverrideActivity(OverrideActivity overrideActivity);
}
