package com.isipathana.meditationcenter.rest.admin.activity.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming Activity domain objects into PostActivityResponse DTOs.
 * Implements the ResponseBuilder interface to create responses.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class PostActivityPresenter implements PostActivityResponseBuilder {
    
    @Override
    public PostActivityResponse build(Activity activity) {
        return PostActivityResponse.builder()
                .activityId(activity.activityId())
                .title(activity.title())
                .description(activity.description())
                .mediaUrl(activity.mediaUrl())
                .createdAt(activity.createdAt())
                .updatedAt(activity.updatedAt())
                .build();
    }
}
