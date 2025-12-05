package com.isipathana.meditationcenter.rest.admin.activity.post;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.isipathana.meditationcenter.jooq.Tables.ACTIVITY;

/**
 * Repository implementation for creating activities using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PostActivityRepository implements PostActivityDataAccess {
    
    private final DSLContext dslContext;
    
    @Override
    public Activity createActivity(Activity activity) {
        var record = dslContext
                .insertInto(ACTIVITY)
                .set(ACTIVITY.TITLE, activity.title())
                .set(ACTIVITY.DESCRIPTION, activity.description())
                .set(ACTIVITY.MEDIA_URL, activity.mediaUrl())
                .returning(
                        ACTIVITY.ACTIVITY_ID,
                        ACTIVITY.TITLE,
                        ACTIVITY.DESCRIPTION,
                        ACTIVITY.MEDIA_URL,
                        ACTIVITY.CREATED_AT,
                        ACTIVITY.UPDATED_AT
                )
                .fetchOne();
        
        return Activity.builder()
                .activityId(record.get(ACTIVITY.ACTIVITY_ID))
                .title(record.get(ACTIVITY.TITLE))
                .description(record.get(ACTIVITY.DESCRIPTION))
                .mediaUrl(record.get(ACTIVITY.MEDIA_URL))
                .createdAt(record.get(ACTIVITY.CREATED_AT))
                .updatedAt(record.get(ACTIVITY.UPDATED_AT))
                .build();
    }
}
