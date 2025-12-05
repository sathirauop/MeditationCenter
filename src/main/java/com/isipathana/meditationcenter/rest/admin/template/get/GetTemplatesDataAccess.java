package com.isipathana.meditationcenter.rest.admin.template.get;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;

import java.util.stream.Stream;

/**
 * Data access interface for retrieving templates.
 *
 * @author Sathira Basnayake
 */
public interface GetTemplatesDataAccess {

    /**
     * Retrieves paginated templates with activity counts.
     *
     * @param limit the maximum number of results
     * @param offset the pagination offset
     * @return stream of templates
     */
    Stream<ScheduleTemplate> getTemplates(int limit, int offset);

    /**
     * Counts total number of templates.
     *
     * @return total count of templates
     */
    long countTemplates();

    /**
     * Gets the activity count for a template.
     *
     * @param templateId the template ID
     * @return number of activities in the template
     */
    int getActivityCount(Long templateId);
}
