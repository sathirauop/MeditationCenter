package com.isipathana.meditationcenter.rest.admin.activity.patch;

import com.isipathana.meditationcenter.records.schedule.Activity;

import java.util.Optional;

/**
 * Data access interface for updating activities.
 *
 * @author Sathira Basnayake
 */
public interface PatchActivityDataAccess {
    /**
     * Finds an activity by its ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found, empty otherwise
     */
    Optional<Activity> findActivityById(Long activityId);
    
    /**
     * Updates an activity.
     *
     * @param activity the activity to update
     * @return the updated activity
     */
    Activity updateActivity(Activity activity);
}
