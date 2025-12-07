package com.isipathana.meditationcenter.rest.admin.override.get;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.isipathana.meditationcenter.jooq.Tables.*;
import static org.jooq.impl.DSL.count;

/**
 * Repository implementation for retrieving schedule overrides.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetOverridesRepository implements GetOverridesDataAccess {

    private final DSLContext dslContext;

    @Override
    public List<GetOverridesResponse> getOverrides(int offset, int limit, LocalDate fromDate, LocalDate toDate) {
        List<Condition> conditions = buildConditions(fromDate, toDate);

        return dslContext
                .select(
                        SCHEDULE_OVERRIDE.OVERRIDE_ID,
                        SCHEDULE_OVERRIDE.OVERRIDE_DATE,
                        count(OVERRIDE_ACTIVITY.ID).as("activity_count"),
                        SCHEDULE_OVERRIDE.CREATED_AT,
                        SCHEDULE_OVERRIDE.UPDATED_AT
                )
                .from(SCHEDULE_OVERRIDE)
                .leftJoin(OVERRIDE_ACTIVITY)
                .on(SCHEDULE_OVERRIDE.OVERRIDE_ID.eq(OVERRIDE_ACTIVITY.OVERRIDE_ID))
                .where(conditions)
                .groupBy(
                        SCHEDULE_OVERRIDE.OVERRIDE_ID,
                        SCHEDULE_OVERRIDE.OVERRIDE_DATE,
                        SCHEDULE_OVERRIDE.CREATED_AT,
                        SCHEDULE_OVERRIDE.UPDATED_AT
                )
                .orderBy(SCHEDULE_OVERRIDE.OVERRIDE_DATE.desc())
                .limit(limit)
                .offset(offset)
                .fetch(record -> new GetOverridesResponse(
                        record.get(SCHEDULE_OVERRIDE.OVERRIDE_ID),
                        record.get(SCHEDULE_OVERRIDE.OVERRIDE_DATE),
                        record.get("activity_count", Integer.class),
                        record.get(SCHEDULE_OVERRIDE.CREATED_AT),
                        record.get(SCHEDULE_OVERRIDE.UPDATED_AT)
                ));
    }

    @Override
    public int countOverrides(LocalDate fromDate, LocalDate toDate) {
        List<Condition> conditions = buildConditions(fromDate, toDate);

        Integer count = dslContext
                .selectCount()
                .from(SCHEDULE_OVERRIDE)
                .where(conditions)
                .fetchOne(0, int.class);

        return count != null ? count : 0;
    }

    private List<Condition> buildConditions(LocalDate fromDate, LocalDate toDate) {
        List<Condition> conditions = new ArrayList<>();

        if (fromDate != null) {
            conditions.add(SCHEDULE_OVERRIDE.OVERRIDE_DATE.greaterOrEqual(fromDate));
        }

        if (toDate != null) {
            conditions.add(SCHEDULE_OVERRIDE.OVERRIDE_DATE.lessOrEqual(toDate));
        }

        return conditions;
    }
}
