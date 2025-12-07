package com.isipathana.meditationcenter.rest.admin.template.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;

import java.util.stream.Stream;

/**
 * Response builder interface for template list retrieval.
 *
 * @author Sathira Basnayake
 */
public interface GetTemplatesResponseBuilder {

    /**
     * Builds paginated response for templates.
     *
     * @param templates stream of templates
     * @param currentOffset the current pagination offset
     * @param maxOffset the maximum offset (total count - 1)
     * @return paginated response
     */
    OffsetSearchResponse<GetTemplatesResponse> build(
            Stream<ScheduleTemplate> templates,
            long currentOffset,
            long maxOffset
    );
}
