package com.isipathana.meditationcenter.rest.admin.template.get;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

/**
 * UseCase for retrieving paginated templates.
 * Handles business logic for template list retrieval with activity counts.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetTemplatesUseCase implements UseCase<GetTemplatesRequest, OffsetSearchResponse<GetTemplatesResponse>> {

    private final GetTemplatesDataAccess repository;
    private final GetTemplatesResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    @Override
    public OffsetSearchResponse<GetTemplatesResponse> handle(GetTemplatesRequest request) {
        log.info("Fetching templates with limit {} and offset {}", request.limit(), request.offset());

        long totalCount = repository.countTemplates();
        long maxOffset = Math.max(0, totalCount - 1);

        Stream<ScheduleTemplate> templates = repository.getTemplates(request.limit(), request.offset());

        return responseBuilder.build(templates, request.offset(), maxOffset);
    }
}
