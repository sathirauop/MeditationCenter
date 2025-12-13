package com.isipathana.meditationcenter.rest.program;

import com.isipathana.meditationcenter.constants.EndPoints;
import com.isipathana.meditationcenter.rest.program.get.GetProgramResponse;
import com.isipathana.meditationcenter.rest.program.get.GetProgramUseCase;
import com.isipathana.meditationcenter.rest.program.getActive.GetActiveProgramResponse;
import com.isipathana.meditationcenter.rest.program.getActive.GetActiveProgramUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for public meditation program endpoints.
 * No authentication required.
 *
 * @author Sathira Basnayake
 */
@RestController
@RequestMapping(EndPoints.Public.Program.BASE)
@RequiredArgsConstructor
public class ProgramController {

    private final GetProgramUseCase getProgramUseCase;
    private final GetActiveProgramUseCase getActiveProgramUseCase;

    /**
     * Get the currently active meditation program (public).
     * Returns the single active program without needing to know the ID.
     * <p>
     * GET /api/programs/active
     * <p>
     * No authentication required.
     *
     * @return 200 OK with active program details (includes presigned image URLs)
     */
    @GetMapping(EndPoints.Public.Program.GET_ACTIVE)
    public ResponseEntity<GetActiveProgramResponse> getActiveProgram() {
        GetActiveProgramResponse response = getActiveProgramUseCase.execute();
        return ResponseEntity.ok(response);
    }

    /**
     * Get an active meditation program by ID (public).
     * Only returns programs where isActive = true.
     * <p>
     * GET /api/programs/{programId}
     * <p>
     * No authentication required.
     *
     * @param programId The program ID
     * @return 200 OK with program details (includes presigned image URLs)
     */
    @GetMapping(EndPoints.Public.Program.GET_BY_ID)
    public ResponseEntity<GetProgramResponse> getProgram(@PathVariable Long programId) {
        GetProgramResponse response = getProgramUseCase.execute(programId);
        return ResponseEntity.ok(response);
    }
}
