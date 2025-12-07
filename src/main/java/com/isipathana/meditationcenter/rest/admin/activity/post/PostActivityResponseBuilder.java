package com.isipathana.meditationcenter.rest.admin.activity.post;

import com.isipathana.meditationcenter.records.schedule.Activity;

/**
 * Builder interface for creating PostActivity response.
 * Transforms an Activity domain object into PostActivityResponse.
 *
 * @author Sathira Basnayake
 */
public interface PostActivityResponseBuilder {
    
    /**
     * Build response from activity domain object.
     *
     * @param activity Activity domain object
     * @return PostActivityResponse DTO
     */
    PostActivityResponse build(Activity activity);
}
