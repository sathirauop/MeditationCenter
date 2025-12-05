package com.isipathana.meditationcenter.rest.admin.override.getbydate;

import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.*;

/**
 * Repository implementation for getting override by date.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetOverrideByDateRepository implements GetOverrideByDateDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<ScheduleOverride> findOverrideByDate(LocalDate date) {
        return dslContext.selectFrom(SCHEDULE_OVERRIDE)
                .where(SCHEDULE_OVERRIDE.OVERRIDE_DATE.eq(date))
                .fetchOptional(record -> ScheduleOverride.builder()
                        .overrideId(record.getOverrideId())
                        .overrideDate(record.getOverrideDate())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build());
    }

    @Override
    public List<GetOverrideByDateResponse.OverrideActivityDetail> getOverrideActivities(Long overrideId) {
        return dslContext
                .select(
                        OVERRIDE_ACTIVITY.ID,
                        OVERRIDE_ACTIVITY.ACTIVITY_ID,
                        ACTIVITY.TITLE,
                        ACTIVITY.DESCRIPTION,
                        OVERRIDE_ACTIVITY.START_TIME,
                        OVERRIDE_ACTIVITY.END_TIME,
                        OVERRIDE_ACTIVITY.NOTES
                )
                .from(OVERRIDE_ACTIVITY)
                .join(ACTIVITY).on(OVERRIDE_ACTIVITY.ACTIVITY_ID.eq(ACTIVITY.ACTIVITY_ID))
                .where(OVERRIDE_ACTIVITY.OVERRIDE_ID.eq(overrideId))
                .orderBy(OVERRIDE_ACTIVITY.START_TIME.asc())
                .fetch(record -> new GetOverrideByDateResponse.OverrideActivityDetail(
                        record.get(OVERRIDE_ACTIVITY.ID),
                        record.get(OVERRIDE_ACTIVITY.ACTIVITY_ID),
                        record.get(ACTIVITY.TITLE),
                        record.get(ACTIVITY.DESCRIPTION),
                        record.get(OVERRIDE_ACTIVITY.START_TIME).toString(),
                        record.get(OVERRIDE_ACTIVITY.END_TIME).toString(),
                        record.get(OVERRIDE_ACTIVITY.NOTES)
                ));
    }
}
