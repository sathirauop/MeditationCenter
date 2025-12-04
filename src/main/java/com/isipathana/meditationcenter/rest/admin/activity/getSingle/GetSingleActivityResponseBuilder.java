package com.isipathana.meditationcenter.rest.admin.activity.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;

/**
 * Builder interface for creating GetSingleActivity response.
 * Transforms an Activity domain object into GetSingleActivityResponse.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleActivityResponseBuilder {
    
    /**
     * Build response from activity domain object.
     *
     * @param activity Activity domain object
     * @return GetSingleActivityResponse DTO
     */
    GetSingleActivityResponse build(Activity activity);
}
