package com.isipathana.meditationcenter.rest.admin.activity.delete;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.ACTIVITY;

/**
 * Repository implementation for deleting activities using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class DeleteActivityRepository implements DeleteActivityDataAccess {
    
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
    public boolean deleteActivity(Long activityId) {
        int deletedCount = dslContext
                .deleteFrom(ACTIVITY)
                .where(ACTIVITY.ACTIVITY_ID.eq(activityId))
                .execute();
        
        return deletedCount > 0;
    }
}
