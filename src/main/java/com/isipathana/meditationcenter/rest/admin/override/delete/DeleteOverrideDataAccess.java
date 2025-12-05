package com.isipathana.meditationcenter.rest.admin.override.delete;

import com.isipathana.meditationcenter.records.schedule.ScheduleOverride;

import java.util.Optional;

/**
 * Data access interface for deleting overrides.
 *
 * @author Sathira Basnayake
 */
public interface DeleteOverrideDataAccess {
    Optional<ScheduleOverride> findOverrideById(Long overrideId);
    void deleteOverride(Long overrideId);
}
