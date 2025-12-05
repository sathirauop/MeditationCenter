package com.isipathana.meditationcenter.rest.schedule.get;

import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data access interface for getting schedule.
 *
 * @author Sathira Basnayake
 */
public interface GetScheduleDataAccess {
    Optional<ScheduleOverride> findOverrideByDate(LocalDate date);
    List<GetScheduleResponse.ScheduleActivity> getOverrideActivities(Long overrideId);
    Optional<ScheduleTemplate> findActiveTemplate();
    List<GetScheduleResponse.ScheduleActivity> getTemplateActivities(Long templateId);
}
