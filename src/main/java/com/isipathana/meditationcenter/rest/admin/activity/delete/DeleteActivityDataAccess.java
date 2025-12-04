package com.isipathana.meditationcenter.rest.admin.activity.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;

import java.util.Optional;

/**
 * Data access interface for deleting activities.
 *
 * @author Sathira Basnayake
 */
public interface DeleteActivityDataAccess {
    /**
     * Finds an activity by its ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found, empty otherwise
     */
    Optional<Activity> findActivityById(Long activityId);
    
    /**
     * Deletes an activity by its ID.
     *
     * @param activityId the activity ID
     * @return true if deleted, false otherwise
     */
    boolean deleteActivity(Long activityId);
}
