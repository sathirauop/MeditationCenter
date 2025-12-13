package com.isipathana.meditationcenter.records.program;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Domain model representing a meditation program.
 * Programs can have cover images and gallery images stored in R2.
 *
 * @author Sathira Basnayake
 */
@Builder
public record MeditationProgram(
    Long meditationProgramId,
    String name,
    String description,
    Integer maxSeats,
    String coverImageKey,
    Set<String> galleryImageKeys,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
