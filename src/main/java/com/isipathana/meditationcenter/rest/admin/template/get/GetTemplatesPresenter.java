package com.isipathana.meditationcenter.rest.admin.template.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for transforming template data into list response format.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetTemplatesPresenter implements GetTemplatesResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;
    private final GetTemplatesDataAccess repository;

    @Override
    public OffsetSearchResponse<GetTemplatesResponse> build(
            Stream<ScheduleTemplate> templates,
            long currentOffset,
            long maxOffset) {

        List<GetTemplatesResponse> responseList = templates
                .map(this::mapTemplateToResponse)
                .toList();

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }

    private GetTemplatesResponse mapTemplateToResponse(ScheduleTemplate template) {
        int activityCount = repository.getActivityCount(template.templateId());

        return new GetTemplatesResponse(
                template.templateId(),
                template.name(),
                template.description(),
                template.isActive(),
                activityCount,
                template.createdAt(),
                template.updatedAt()
        );
    }
}
