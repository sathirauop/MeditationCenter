package com.isipathana.meditationcenter.rest.admin.template.activate;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming template activation data into response format.
 *
 * @author Sathira Basnayake
 */
@Component
public class ActivateTemplatePresenter implements ActivateTemplateResponseBuilder {

    @Override
    public ActivateTemplateResponse build(
            ScheduleTemplate activatedTemplate,
            Long previousActiveTemplateId) {

        String message = previousActiveTemplateId != null
                ? String.format("Template '%s' activated. Previous template (ID: %d) deactivated.",
                activatedTemplate.name(), previousActiveTemplateId)
                : String.format("Template '%s' activated.", activatedTemplate.name());

        return new ActivateTemplateResponse(
                activatedTemplate.templateId(),
                activatedTemplate.name(),
                activatedTemplate.isActive(),
                previousActiveTemplateId,
                activatedTemplate.updatedAt(),
                message
        );
    }
}
