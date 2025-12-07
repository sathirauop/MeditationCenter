package com.isipathana.meditationcenter.rest.admin.activity.get;

import com.isipathana.meditationcenter.records.schedule.Activity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.isipathana.meditationcenter.jooq.Tables.ACTIVITY;

/**
 * Repository implementation for retrieving activities using jOOQ.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetActivitiesRepository implements GetActivitiesDataAccess {
    
    private final DSLContext dslContext;
    
    @Override
    public List<Activity> findActivities(int offset, int limit) {
        return dslContext
                .selectFrom(ACTIVITY)
                .orderBy(ACTIVITY.CREATED_AT.desc())
                .limit(limit)
                .offset(offset)
                .fetch(record -> Activity.builder()
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
    public long getActivityCount() {
        return dslContext
                .selectCount()
                .from(ACTIVITY)
                .fetchOne(0, long.class);
    }
}
