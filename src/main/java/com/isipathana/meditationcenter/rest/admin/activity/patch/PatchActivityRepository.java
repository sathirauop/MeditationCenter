package com.isipathana.meditationcenter.rest.admin.activity.patch;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.ACTIVITY;

/**
 * Repository implementation for updating activities using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class PatchActivityRepository implements PatchActivityDataAccess {
    
    private final DSLContext dslContext;
    
    @Override
    public Optional<Activity> findActivityById(Long activityId) {
        return dslContext
                .selectFrom(ACTIVITY)
                .where(ACTIVITY.ACTIVITY_ID.eq(activityId))
                .fetchOptional(record -> Activity.builder()
                        .activityId(record.get(ACTIVITY.ACTIVITY_ID))
                        .title(record.get(ACTIVITY.TITLE))
                        .description(record.get(ACTIVITY.DESCRIPTION))
                        .mediaUrl(record.get(ACTIVITY.MEDIA_URL))
                        .createdAt(record.get(ACTIVITY.CREATED_AT))
                        .updatedAt(record.get(ACTIVITY.UPDATED_AT))
                        .build()
                );
    }
    
    @Override
    public Activity updateActivity(Activity activity) {
        var record = dslContext
                .update(ACTIVITY)
                .set(ACTIVITY.TITLE, activity.title())
                .set(ACTIVITY.DESCRIPTION, activity.description())
                .set(ACTIVITY.MEDIA_URL, activity.mediaUrl())
                .where(ACTIVITY.ACTIVITY_ID.eq(activity.activityId()))
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
