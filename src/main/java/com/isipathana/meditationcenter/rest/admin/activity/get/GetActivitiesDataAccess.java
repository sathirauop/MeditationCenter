package com.isipathana.meditationcenter.rest.admin.activity.get;

import com.isipathana.meditationcenter.records.schedule.Activity;

import java.util.List;

/**
 * Data access interface for retrieving activities.
 *
 * @author Sathira Basnayake
 */
public interface GetActivitiesDataAccess {
    /**
     * Finds all activities with pagination.
     *
     * @param offset the starting position
     * @param limit the maximum number of results
     * @return list of activities
     */
    List<Activity> findActivities(int offset, int limit);
    
    /**
     * Gets the total count of all activities.
     *
     * @return total activity count
     */
    long getActivityCount();
}
