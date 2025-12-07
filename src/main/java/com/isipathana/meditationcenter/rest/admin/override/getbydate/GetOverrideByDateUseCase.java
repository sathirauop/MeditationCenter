package com.isipathana.meditationcenter.rest.admin.override.getbydate;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import com.isipathana.meditationcenter.architecture.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for getting override by date.
 *
 * @author Sathira Basnayake
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetOverrideByDateUseCase implements UseCase<GetOverrideByDateRequest, GetOverrideByDateResponse> {

    private final GetOverrideByDateDataAccess repository;
    private final GetOverrideByDateResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    @Override
    public GetOverrideByDateResponse handle(GetOverrideByDateRequest request) {
        log.info("Fetching override for date: {}", request.date());

        ScheduleOverride override = repository.findOverrideByDate(request.date())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No override found for date: " + request.date()
                ));

        List<GetOverrideByDateResponse.OverrideActivityDetail> activities =
                repository.getOverrideActivities(override.overrideId());

        return responseBuilder.build(override, activities);
    }
}
