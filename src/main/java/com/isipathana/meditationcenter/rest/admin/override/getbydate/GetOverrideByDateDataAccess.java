package com.isipathana.meditationcenter.rest.admin.override.getbydate;

import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data access interface for getting override by date.
 *
 * @author Sathira Basnayake
 */
public interface GetOverrideByDateDataAccess {
    Optional<ScheduleOverride> findOverrideByDate(LocalDate date);
    List<GetOverrideByDateResponse.OverrideActivityDetail> getOverrideActivities(Long overrideId);
}
