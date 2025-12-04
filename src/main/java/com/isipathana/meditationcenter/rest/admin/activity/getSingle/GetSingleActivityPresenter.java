package com.isipathana.meditationcenter.rest.admin.activity.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming Activity domain objects into GetSingleActivityResponse DTOs.
 * Implements the ResponseBuilder interface to create responses.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetSingleActivityPresenter implements GetSingleActivityResponseBuilder {
    
    @Override
    public GetSingleActivityResponse build(Activity activity) {
        return GetSingleActivityResponse.builder()
                .activityId(activity.activityId())
                .title(activity.title())
                .description(activity.description())
                .mediaUrl(activity.mediaUrl())
                .createdAt(activity.createdAt())
                .updatedAt(activity.updatedAt())
                .build();
    }
}
