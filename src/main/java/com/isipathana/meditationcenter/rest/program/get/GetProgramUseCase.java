package com.isipathana.meditationcenter.rest.program.get;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.program.MeditationProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for getting an active meditation program by ID (public).
 * Handles business logic for retrieving program details with presigned URLs.
 * Only returns active programs.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetProgramUseCase {

    private final GetProgramDataAccess repository;
    private final GetProgramResponseBuilder responseBuilder;

    @Transactional(readOnly = true)
    public GetProgramResponse execute(Long programId) {
        log.info("Fetching active meditation program with ID: {}", programId);

        MeditationProgram program = repository.findActiveById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Active meditation program not found with ID: " + programId));

        log.info("Active program found: {}", program.name());

        return responseBuilder.build(program);
    }
}
