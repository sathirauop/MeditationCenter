package com.isipathana.meditationcenter.rest.admin.activity.post;

import com.isipathana.meditationcenter.records.schedule.Activity;

/**
 * Data access interface for creating activities.
 *
 * @author Sathira Basnayake
 */
public interface PostActivityDataAccess {
    /**
     * Creates a new activity.
     *
     * @param activity the activity to create
     * @return the created activity with generated ID and timestamps
     */
    Activity createActivity(Activity activity);
}
