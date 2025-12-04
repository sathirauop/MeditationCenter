package com.isipathana.meditationcenter.rest.admin.template.activate;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;

import java.util.Optional;

/**
 * Data access interface for template activation operations.
 *
 * @author Sathira Basnayake
 */
public interface ActivateTemplateDataAccess {

    /**
     * Finds a template by ID.
     *
     * @param templateId the template ID
     * @return Optional containing the template if found
     */
    Optional<ScheduleTemplate> findTemplateById(Long templateId);

    /**
     * Finds the currently active template.
     *
     * @return Optional containing the active template if found
     */
    Optional<ScheduleTemplate> findActiveTemplate();

    /**
     * Deactivates all templates.
     *
     * @return number of templates deactivated
     */
    int deactivateAllTemplates();

    /**
     * Activates a specific template.
     *
     * @param templateId the template ID to activate
     * @return the activated template
     */
    ScheduleTemplate activateTemplate(Long templateId);
}
