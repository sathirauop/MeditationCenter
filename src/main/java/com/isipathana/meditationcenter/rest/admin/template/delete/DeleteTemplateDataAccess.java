package com.isipathana.meditationcenter.rest.admin.template.delete;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;

import java.util.Optional;

/**
 * Data access interface for template deletion operations.
 *
 * @author Sathira Basnayake
 */
public interface DeleteTemplateDataAccess {

    /**
     * Finds a template by ID.
     *
     * @param templateId the template ID
     * @return Optional containing the template if found
     */
    Optional<ScheduleTemplate> findTemplateById(Long templateId);

    /**
     * Deletes a template by ID.
     * Note: Associated template_schedule_activity records will be deleted automatically
     * via ON DELETE CASCADE constraint.
     *
     * @param templateId the template ID
     * @return true if deleted, false otherwise
     */
    boolean deleteTemplate(Long templateId);
}
