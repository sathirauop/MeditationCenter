package com.isipathana.meditationcenter.rest.admin.activity.getSingle;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.ACTIVITY;

/**
 * Repository implementation for retrieving a single activity using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetSingleActivityRepository implements GetSingleActivityDataAccess {
    
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
}
