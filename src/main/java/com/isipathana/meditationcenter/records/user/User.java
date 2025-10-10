package com.isipathana.meditationcenter.records.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record User(
    Long userId,
    String email,
    String name,
    String mobileNumber,
    UserRole role,
    Boolean isActive,
    Boolean emailVerified,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
