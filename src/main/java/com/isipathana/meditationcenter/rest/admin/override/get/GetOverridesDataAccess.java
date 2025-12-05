package com.isipathana.meditationcenter.rest.admin.override.get;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access interface for retrieving schedule overrides.
 *
 * @author Sathira Basnayake
 */
public interface GetOverridesDataAccess {
    /**
     * Get overrides with pagination and optional date filtering.
     */
    List<GetOverridesResponse> getOverrides(int offset, int limit, LocalDate fromDate, LocalDate toDate);

    /**
     * Count total overrides matching the filters.
     */
    int countOverrides(LocalDate fromDate, LocalDate toDate);
}
