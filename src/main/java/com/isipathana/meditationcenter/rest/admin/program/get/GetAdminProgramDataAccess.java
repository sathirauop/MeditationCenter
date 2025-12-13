package com.isipathana.meditationcenter.rest.admin.program.get;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

import java.util.Optional;

/**
 * Data access interface for getting meditation programs (admin).
 *
 * @author Sathira Basnayake
 */
public interface GetAdminProgramDataAccess {

    /**
     * Finds a meditation program by ID (no active filter - returns any program).
     *
     * @param programId The program ID
     * @return The program if found
     */
    Optional<MeditationProgram> findById(Long programId);
}
