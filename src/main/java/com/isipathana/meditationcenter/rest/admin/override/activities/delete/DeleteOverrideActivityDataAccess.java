package com.isipathana.meditationcenter.rest.admin.override.activities.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;

import java.util.Optional;

/**
 * Data access interface for deleting override activities.
 *
 * @author Sathira Basnayake
 */
public interface DeleteOverrideActivityDataAccess {
    Optional<OverrideActivity> findOverrideActivityById(Long overrideId, Long overrideActivityId);
    Optional<Activity> findActivityById(Long activityId);
    void deleteOverrideActivity(Long overrideActivityId);
}
