package com.isipathana.meditationcenter.records.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventRegistration(
    Long registrationId,
    Long eventId,
    Long userId,
    EventRegistrationStatus status,
    LocalDateTime checkedInAt,
    LocalDateTime registrationDate,
    LocalDateTime cancellationDate,
    String cancellationReason
) {}
