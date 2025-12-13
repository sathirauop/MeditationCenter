package com.isipathana.meditationcenter.rest.admin.program.patch;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

/**
 * Response builder interface for transforming MeditationProgram to PatchProgramResponse.
 *
 * @author Sathira Basnayake
 */
public interface PatchProgramResponseBuilder {

    /**
     * Builds a response from a meditation program.
     *
     * @param program The meditation program
     * @return The response with presigned URLs
     */
    PatchProgramResponse build(MeditationProgram program);
}
