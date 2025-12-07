package com.isipathana.meditationcenter.rest.admin.activity.patch;

import com.isipathana.meditationcenter.records.schedule.Activity;

/**
 * Builder interface for creating PatchActivity response.
 * Transforms an Activity domain object into PatchActivityResponse.
 *
 * @author Sathira Basnayake
 */
public interface PatchActivityResponseBuilder {
    
    /**
     * Build response from activity domain object.
     *
     * @param activity Activity domain object
     * @return PatchActivityResponse DTO
     */
    PatchActivityResponse build(Activity activity);
}
