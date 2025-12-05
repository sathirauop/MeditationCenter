package com.isipathana.meditationcenter.rest.admin.override.activities.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import com.isipathana.meditationcenter.records.schedule.OverrideActivity;
import org.springframework.stereotype.Component;

/**
 * Builds PostOverrideActivityResponse from domain objects.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostOverrideActivityResponseBuilder {

    public PostOverrideActivityResponse build(OverrideActivity overrideActivity, Activity activity) {
        return new PostOverrideActivityResponse(
                overrideActivity.id(),
                overrideActivity.overrideId(),
                activity.activityId(),
                activity.title(),
                overrideActivity.startTime().toString(),
                overrideActivity.endTime().toString(),
                overrideActivity.notes(),
                overrideActivity.createdAt()
        );
    }
}
