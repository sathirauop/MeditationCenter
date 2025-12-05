package com.isipathana.meditationcenter.rest.admin.override.put;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;

import java.util.List;
import java.util.Optional;

/**
 * Data access interface for updating overrides.
 *
 * @author Sathira Basnayake
 */
public interface PutOverrideDataAccess {
    Optional<ScheduleOverride> findOverrideById(Long overrideId);
    ScheduleOverride updateOverride(ScheduleOverride override);
    int deleteAllOverrideActivities(Long overrideId);
    OverrideActivity createOverrideActivity(OverrideActivity overrideActivity);
    List<Activity> findActivitiesByIds(List<Long> activityIds);
}
