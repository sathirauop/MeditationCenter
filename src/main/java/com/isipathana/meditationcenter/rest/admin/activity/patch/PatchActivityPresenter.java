package com.isipathana.meditationcenter.rest.admin.activity.patch;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming Activity domain objects into PatchActivityResponse DTOs.
 * Implements the ResponseBuilder interface to create responses.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class PatchActivityPresenter implements PatchActivityResponseBuilder {
    
    @Override
    public PatchActivityResponse build(Activity activity) {
        return PatchActivityResponse.builder()
                .activityId(activity.activityId())
                .title(activity.title())
                .description(activity.description())
                .mediaUrl(activity.mediaUrl())
                .createdAt(activity.createdAt())
                .updatedAt(activity.updatedAt())
                .build();
    }
}
