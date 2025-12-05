package com.isipathana.meditationcenter.rest.schedule.get;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * UseCase for getting schedule for a specific date.
 * Checks for override first, then falls back to active template.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetScheduleUseCase implements UseCase<GetScheduleRequest, GetScheduleResponse> {

    private final GetScheduleDataAccess repository;
    private final GetScheduleResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    @Override
    public GetScheduleResponse handle(GetScheduleRequest request) {
        log.info("Fetching schedule for date: {}", request.date());

        // First, check if there's an override for this date
        Optional<ScheduleOverride> override = repository.findOverrideByDate(request.date());

        if (override.isPresent()) {
            log.debug("Found override for date: {}", request.date());
            List<GetScheduleResponse.ScheduleActivity> activities =
                    repository.getOverrideActivities(override.get().overrideId());
            return responseBuilder.buildOverrideSchedule(request.date(), activities);
        }

        // No override, use active template
        Optional<ScheduleTemplate> activeTemplate = repository.findActiveTemplate();

        if (activeTemplate.isPresent()) {
            log.debug("Using active template: {}", activeTemplate.get().name());
            List<GetScheduleResponse.ScheduleActivity> activities =
                    repository.getTemplateActivities(activeTemplate.get().templateId());
            return responseBuilder.buildTemplateSchedule(
                    request.date(),
                    activeTemplate.get().name(),
                    activities
            );
        }

        // No override and no active template
        log.warn("No schedule found for date: {}", request.date());
        return responseBuilder.buildEmptySchedule(request.date());
    }
}
