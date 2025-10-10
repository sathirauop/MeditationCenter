package com.isipathana.meditationcenter.records.program;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MeditationProgram(
    Long meditationProgramId,
    String name,
    String description,
    Integer maxSeats,
    Integer durationMinutes,
    Long instructorId,
    String imageUrl,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
