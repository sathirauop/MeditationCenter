package com.isipathana.meditationcenter.rest.schedule.get;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Builds GetScheduleResponse from domain objects.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetScheduleResponseBuilder {

    public GetScheduleResponse buildOverrideSchedule(
            LocalDate date,
            List<GetScheduleResponse.ScheduleActivity> activities
    ) {
        return new GetScheduleResponse(
                date,
                GetScheduleResponse.ScheduleType.OVERRIDE,
                "Special Schedule",
                activities
        );
    }

    public GetScheduleResponse buildTemplateSchedule(
            LocalDate date,
            String templateName,
            List<GetScheduleResponse.ScheduleActivity> activities
    ) {
        return new GetScheduleResponse(
                date,
                GetScheduleResponse.ScheduleType.TEMPLATE,
                templateName,
                activities
        );
    }

    public GetScheduleResponse buildEmptySchedule(LocalDate date) {
        return new GetScheduleResponse(
                date,
                GetScheduleResponse.ScheduleType.NONE,
                "No Schedule",
                Collections.emptyList()
        );
    }
}
