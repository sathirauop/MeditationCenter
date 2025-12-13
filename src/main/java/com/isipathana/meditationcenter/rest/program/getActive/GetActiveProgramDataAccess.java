package com.isipathana.meditationcenter.rest.program.getActive;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

import java.util.Optional;

/**
 * Data access interface for getting the active meditation program (public).
 *
 * @author Sathira Basnayake
 */
public interface GetActiveProgramDataAccess {

    /**
     * Finds the currently active meditation program.
     * Returns the program where isActive = true.
     * If multiple programs are active (should not happen), returns the most recently created one.
     *
     * @return The active program if found
     */
    Optional<MeditationProgram> findActiveProgram();
}
