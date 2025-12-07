package com.isipathana.meditationcenter.rest.admin.override.getbydate;

import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds GetOverrideByDateResponse from domain objects.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetOverrideByDateResponseBuilder {

    public GetOverrideByDateResponse build(
            ScheduleOverride override,
            List<GetOverrideByDateResponse.OverrideActivityDetail> activities
    ) {
        return new GetOverrideByDateResponse(
                override.overrideId(),
                override.overrideDate(),
                activities,
                override.createdAt(),
                override.updatedAt()
        );
    }
}
