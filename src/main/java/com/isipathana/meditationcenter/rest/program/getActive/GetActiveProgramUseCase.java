package com.isipathana.meditationcenter.rest.program.getActive;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for getting the active meditation program (public).
 * Handles business logic for retrieving the currently active program with presigned URLs.
 * Only returns active programs.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetActiveProgramUseCase {

    private final GetActiveProgramDataAccess repository;
    private final GetActiveProgramResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    public GetActiveProgramResponse execute() {
        log.info("Fetching active meditation program");

        MeditationProgram program = repository.findActiveProgram()
                .orElseThrow(() -> new ResourceNotFoundException("No active meditation program found"));

        log.info("Active program found: {}", program.name());

        return responseBuilder.build(program);
    }
}
