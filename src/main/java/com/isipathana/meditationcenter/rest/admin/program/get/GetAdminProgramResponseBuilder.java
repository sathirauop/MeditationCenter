package com.isipathana.meditationcenter.rest.admin.program.get;

import com.isipathana.meditationcenter.records.program.MeditationProgram;

/**
 * Response builder interface for transforming MeditationProgram to GetAdminProgramResponse.
 *
 * @author Sathira Basnayake
 */
public interface GetAdminProgramResponseBuilder {

    /**
     * Builds a response from a meditation program.
     *
     * @param program The meditation program
     * @return The response with presigned URLs
     */
    GetAdminProgramResponse build(MeditationProgram program);
}
