package com.isipathana.meditationcenter.rest.admin.activity.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;

import java.util.Optional;

/**
 * Data access interface for retrieving a single activity by ID.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleActivityDataAccess {
    /**
     * Finds an activity by its ID.
     *
     * @param activityId the activity ID
     * @return Optional containing the activity if found, empty otherwise
     */
    Optional<Activity> findActivityById(Long activityId);
}
