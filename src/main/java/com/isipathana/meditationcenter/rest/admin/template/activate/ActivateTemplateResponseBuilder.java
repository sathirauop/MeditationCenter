package com.isipathana.meditationcenter.rest.admin.template.activate;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;

/**
 * Response builder interface for template activation.
 *
 * @author Sathira Basnayake
 */
public interface ActivateTemplateResponseBuilder {

    /**
     * Builds the response for template activation.
     *
     * @param activatedTemplate the activated template
     * @param previousActiveTemplateId ID of the previously active template (null if none)
     * @return the formatted response
     */
    ActivateTemplateResponse build(
            ScheduleTemplate activatedTemplate,
            Long previousActiveTemplateId
    );
}
