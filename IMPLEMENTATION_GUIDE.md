# Daily Schedule Management - Implementation Guide

**Author:** Sathira Basnayake
**Date:** December 4, 2025
**Status:** Activity CRUD Complete | Template/Override Implementation Guide

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture Pattern](#architecture-pattern)
3. [Completed Work](#completed-work)
4. [Implementation Templates](#implementation-templates)
5. [Phase 3: Template CRUD](#phase-3-template-crud)
6. [Phase 4: Template Activity Management](#phase-4-template-activity-management)
7. [Phase 5: Override CRUD](#phase-5-override-crud)
8. [Phase 6: Override Activity Management](#phase-6-override-activity-management)
9. [Phase 7: Public Schedule Preview API](#phase-7-public-schedule-preview-api)
10. [Phase 8: Security Configuration](#phase-8-security-configuration)
11. [Testing Checklist](#testing-checklist)

---

## Overview

This guide provides templates and patterns for implementing the remaining Daily Schedule Management endpoints. All Activity CRUD operations are complete and serve as reference implementations.

### Database Schema

```
activity (master data)
  ↓
schedule_template (default schedule)
  ↓
template_schedule_activity (template → activities with times)

schedule_override (special dates)
  ↓
override_activity (override → activities with times)
```

### Key Principle

**"All or Nothing" Rule**: When an override exists for a date, the ENTIRE schedule comes from `override_activity`, not a mix of template + override.

---

## Architecture Pattern

Every endpoint follows this structure:

```
rest/{feature}/{httpMethod}/
├── {HttpMethod}{Feature}DataAccess.java      (Interface)
├── {HttpMethod}{Feature}Repository.java      (jOOQ Implementation)
├── {HttpMethod}{Feature}Request.java         (Input DTO)
├── {HttpMethod}{Feature}Response.java        (Output DTO)
├── {HttpMethod}{Feature}ResponseBuilder.java (Builder Interface)
├── {HttpMethod}{Feature}Presenter.java       (Builder Implementation)
└── {HttpMethod}{Feature}UseCase.java         (Business Logic)
```

### Component Responsibilities

| Component | Purpose | Key Points |
|-----------|---------|------------|
| **DataAccess** | Interface defining data operations | No implementation details |
| **Repository** | jOOQ queries | Implements DataAccess, maps DB ↔ Domain |
| **Request** | Input validation | Jakarta validation annotations |
| **Response** | Output format | `@JsonProperty` for snake_case |
| **ResponseBuilder** | Interface for transformation | Defines `build()` method |
| **Presenter** | Response transformation | `@Component`, implements ResponseBuilder |
| **UseCase** | Business logic | `@Service`, `@Transactional` |

---

## Completed Work

### ✅ Phase 1-2: Foundation & Activity CRUD

**Files Created: 34 files**

#### Domain Models (5 files)
- `Activity.java`
- `ScheduleTemplate.java`
- `TemplateScheduleActivity.java`
- `ScheduleOverride.java`
- `OverrideActivity.java`

#### Activity Endpoints (29 files)
1. **POST /api/admin/activities** - Create activity
2. **GET /api/admin/activities** - List activities (paginated)
3. **GET /api/admin/activities/{id}** - Get single activity
4. **PATCH /api/admin/activities/{id}** - Update activity (partial)
5. **DELETE /api/admin/activities/{id}** - Delete activity

**Location:** `src/main/java/com/isipathana/meditationcenter/rest/admin/activity/`

All Activity endpoints follow the complete architecture pattern and can be used as reference implementations.

---

## Implementation Templates

### Template 1: Simple GET Endpoint (Single Entity)

**Example:** GET /api/admin/templates/{id}

#### Step 1: DataAccess Interface

```java
package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import java.util.Optional;

/**
 * Data access interface for retrieving a single template.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleTemplateDataAccess {
    Optional<ScheduleTemplate> findTemplateById(Long templateId);
}
```

#### Step 2: Repository Implementation

```java
package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import static com.isipathana.meditationcenter.jooq.Tables.SCHEDULE_TEMPLATE;

/**
 * Repository implementation for retrieving a single template.
 *
 * @author Sathira Basnayake
 */
@Repository
@RequiredArgsConstructor
public class GetSingleTemplateRepository implements GetSingleTemplateDataAccess {

    private final DSLContext dslContext;

    @Override
    public Optional<ScheduleTemplate> findTemplateById(Long templateId) {
        return dslContext
                .selectFrom(SCHEDULE_TEMPLATE)
                .where(SCHEDULE_TEMPLATE.TEMPLATE_ID.eq(templateId))
                .fetchOptional(record -> ScheduleTemplate.builder()
                        .templateId(record.get(SCHEDULE_TEMPLATE.TEMPLATE_ID))
                        .name(record.get(SCHEDULE_TEMPLATE.NAME))
                        .description(record.get(SCHEDULE_TEMPLATE.DESCRIPTION))
                        .isActive(record.get(SCHEDULE_TEMPLATE.IS_ACTIVE))
                        .createdAt(record.get(SCHEDULE_TEMPLATE.CREATED_AT))
                        .updatedAt(record.get(SCHEDULE_TEMPLATE.UPDATED_AT))
                        .build()
                );
    }
}
```

#### Step 3: Request Record

```java
package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for retrieving a single template.
 *
 * @author Sathira Basnayake
 */
public record GetSingleTemplateRequest(
        @NotNull(message = "Template ID is required")
        Long templateId
) {}
```

#### Step 4: Response Record

```java
package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * Response DTO for single template.
 *
 * @author Sathira Basnayake
 */
@Builder
public record GetSingleTemplateResponse(
        @JsonProperty("template_id")
        Long templateId,

        String name,
        String description,

        @JsonProperty("is_active")
        Boolean isActive,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) implements ApiResponse {}
```

#### Step 5: ResponseBuilder Interface

```java
package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;

/**
 * Builder interface for creating GetSingleTemplate response.
 *
 * @author Sathira Basnayake
 */
public interface GetSingleTemplateResponseBuilder {
    GetSingleTemplateResponse build(ScheduleTemplate template);
}
```

#### Step 6: Presenter Implementation

```java
package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming ScheduleTemplate to GetSingleTemplateResponse.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetSingleTemplatePresenter implements GetSingleTemplateResponseBuilder {

    @Override
    public GetSingleTemplateResponse build(ScheduleTemplate template) {
        return GetSingleTemplateResponse.builder()
                .templateId(template.templateId())
                .name(template.name())
                .description(template.description())
                .isActive(template.isActive())
                .createdAt(template.createdAt())
                .updatedAt(template.updatedAt())
                .build();
    }
}
```

#### Step 7: UseCase

```java
package com.isipathana.meditationcenter.rest.admin.template.getSingle;

import com.isipathana.meditationcenter.architecture.UseCase;
import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for retrieving a single template.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetSingleTemplateUseCase implements UseCase<GetSingleTemplateRequest, GetSingleTemplateResponse> {

    private final GetSingleTemplateDataAccess repository;
    private final GetSingleTemplateResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public GetSingleTemplateResponse handle(GetSingleTemplateRequest request) {
        log.info("Fetching template with ID: {}", request.templateId());

        ScheduleTemplate template = repository.findTemplateById(request.templateId())
                .orElseThrow(() -> {
                    log.warn("Template not found with ID: {}", request.templateId());
                    return new ResourceNotFoundException("Template not found with ID: " + request.templateId());
                });

        return responseBuilder.build(template);
    }
}
```

#### Step 8: Controller Method

```java
@GetMapping(EndPoints.Admin.Template.GET_BY_ID)
@PreAuthorize("hasRole('ADMIN') and hasAuthority('VIEW_TEMPLATES')")
public ResponseEntity<GetSingleTemplateResponse> getTemplateById(@PathVariable Long id) {
    GetSingleTemplateRequest request = new GetSingleTemplateRequest(id);
    GetSingleTemplateResponse response = getSingleTemplateUseCase.handle(request);
    return ResponseEntity.ok(response);
}
```

---

### Template 2: POST Endpoint with Complex Object

**Example:** POST /api/admin/templates (with activities)

#### Key Differences from Simple POST:

1. **DTO for nested objects** (e.g., `TemplateActivityDto`)
2. **Multiple repository calls** in UseCase
3. **Transaction spans multiple operations**

#### TemplateActivityDto

```java
package com.isipathana.meditationcenter.rest.admin.template.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * DTO for activity within a template.
 *
 * @author Sathira Basnayake
 */
public record TemplateActivityDto(
        @NotNull(message = "Activity ID is required")
        Long activityId,

        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        String notes
) {}
```

#### PostTemplateRequest

```java
package com.isipathana.meditationcenter.rest.admin.template.post;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request DTO for creating a template with activities.
 *
 * @author Sathira Basnayake
 */
public record PostTemplateRequest(
        @NotBlank(message = "Template name is required")
        @Size(min = 3, max = 255, message = "Template name must be between 3 and 255 characters")
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @NotEmpty(message = "At least one activity is required")
        @Valid
        List<TemplateActivityDto> activities
) {}
```

#### PostTemplateDataAccess

```java
package com.isipathana.meditationcenter.rest.admin.template.post;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;

/**
 * Data access interface for creating templates.
 *
 * @author Sathira Basnayake
 */
public interface PostTemplateDataAccess {
    ScheduleTemplate createTemplate(ScheduleTemplate template);
    TemplateScheduleActivity addActivityToTemplate(TemplateScheduleActivity templateActivity);
}
```

#### PostTemplateUseCase (Complex)

```java
package com.isipathana.meditationcenter.rest.admin.template.post;

import com.isipathana.meditationcenter.records.schedule.ScheduleTemplate;
import com.isipathana.meditationcenter.records.schedule.TemplateScheduleActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for creating templates with activities.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostTemplateUseCase {

    private final PostTemplateDataAccess repository;
    private final PostTemplateResponseBuilder responseBuilder;

    @Transactional
    public PostTemplateResponse execute(PostTemplateRequest request) {
        log.info("Creating new template: {}", request.name());

        // Step 1: Validate times
        validateActivityTimes(request.activities());

        // Step 2: Create template
        ScheduleTemplate template = ScheduleTemplate.builder()
                .name(request.name())
                .description(request.description())
                .isActive(false) // New templates start inactive
                .build();

        ScheduleTemplate createdTemplate = repository.createTemplate(template);

        // Step 3: Add activities to template
        for (TemplateActivityDto activityDto : request.activities()) {
            TemplateScheduleActivity templateActivity = TemplateScheduleActivity.builder()
                    .templateId(createdTemplate.templateId())
                    .activityId(activityDto.activityId())
                    .startTime(activityDto.startTime())
                    .endTime(activityDto.endTime())
                    .notes(activityDto.notes())
                    .build();

            repository.addActivityToTemplate(templateActivity);
        }

        log.info("Template created successfully with ID: {}", createdTemplate.templateId());

        return responseBuilder.build(createdTemplate, request.activities().size());
    }

    private void validateActivityTimes(List<TemplateActivityDto> activities) {
        for (TemplateActivityDto activity : activities) {
            if (!activity.endTime().isAfter(activity.startTime())) {
                throw new ValidationException(
                    "End time must be after start time for activity " + activity.activityId()
                );
            }
        }

        // Optional: Check for overlapping times
        // Implementation left as exercise
    }
}
```

---

### Template 3: DELETE Endpoint

**Example:** DELETE /api/admin/templates/{id}

#### DeleteTemplateResponse (with static factory methods)

```java
package com.isipathana.meditationcenter.rest.admin.template.delete;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.isipathana.meditationcenter.models.response.ApiResponse;

/**
 * Response DTO for template deletion.
 *
 * @author Sathira Basnayake
 */
public record DeleteTemplateResponse(
        @JsonProperty("template_id") Long templateId,
        String message,
        boolean success
) implements ApiResponse {

    public static DeleteTemplateResponse success(Long templateId, String templateName) {
        return new DeleteTemplateResponse(
            templateId,
            String.format("Template '%s' (ID: %d) deleted successfully", templateName, templateId),
            true
        );
    }

    public static DeleteTemplateResponse notFound(Long templateId) {
        return new DeleteTemplateResponse(
            templateId,
            String.format("Template with ID %d not found", templateId),
            false
        );
    }
}
```

#### DeleteTemplateUseCase

```java
@Transactional
public DeleteTemplateResponse execute(Long templateId) {
    log.info("Deleting template: {}", templateId);

    // Verify existence
    Optional<ScheduleTemplate> template = repository.findTemplateById(templateId);

    if (template.isEmpty()) {
        log.warn("Template not found: {}", templateId);
        return DeleteTemplateResponse.notFound(templateId);
    }

    // Delete (CASCADE will delete template_schedule_activity rows)
    boolean success = repository.deleteTemplate(templateId);

    if (!success) {
        throw new RuntimeException("Failed to delete template");
    }

    log.info("Template deleted: {}", templateId);
    return DeleteTemplateResponse.success(templateId, template.get().name());
}
```

---

### Template 4: Paginated GET Endpoint

**Example:** GET /api/admin/templates

**Reference:** Look at `GetActivitiesUseCase` - exact same pattern

Key components:
- Request with `limit` and `offset`
- Repository with `findTemplates(offset, limit)` and `getTemplateCount()`
- UseCase calculates `actualOffset = offset * limit`
- ResponseBuilder uses `OffsetSearchResponse.Factory`
- Presenter maps stream to list

---

## Phase 3: Template CRUD

### Endpoints to Implement

| Method | Endpoint | Description | Files |
|--------|----------|-------------|-------|
| POST | `/api/admin/templates` | Create template with activities | 7 files |
| GET | `/api/admin/templates` | List all templates (paginated) | 7 files |
| GET | `/api/admin/templates/active` | Get active template | 7 files |
| GET | `/api/admin/templates/{id}` | Get template with activities | 7 files |
| PUT | `/api/admin/templates/{id}` | Update template (name/desc only) | 7 files |
| PATCH | `/api/admin/templates/{id}/activate` | Set template as active | 7 files |
| DELETE | `/api/admin/templates/{id}` | Delete template | 4 files |

**Total: ~45 files**

### Special Considerations

#### 1. POST Template - Create with Activities

**Business Rules:**
- New templates default to `is_active = false`
- Must include at least 1 activity
- Validate `end_time > start_time` for each activity
- Optional: Check for overlapping times

**Request Structure:**
```json
{
  "name": "Daily Routine",
  "description": "Standard weekday schedule",
  "activities": [
    {
      "activity_id": 1,
      "start_time": "05:00",
      "end_time": "06:30",
      "notes": null
    },
    {
      "activity_id": 2,
      "start_time": "07:00",
      "end_time": "08:00",
      "notes": "Light breakfast"
    }
  ]
}
```

**Response Structure:**
```json
{
  "template_id": 1,
  "name": "Daily Routine",
  "description": "Standard weekday schedule",
  "is_active": false,
  "activity_count": 2,
  "created_at": "2025-12-04T10:30:00",
  "updated_at": "2025-12-04T10:30:00"
}
```

#### 2. GET Template by ID - Include Activities

**Response Structure:**
```json
{
  "template_id": 1,
  "name": "Daily Routine",
  "description": "Standard weekday schedule",
  "is_active": true,
  "activities": [
    {
      "id": 1,
      "activity_id": 1,
      "activity_name": "Morning Meditation",
      "start_time": "05:00",
      "end_time": "06:30",
      "notes": null
    },
    {
      "id": 2,
      "activity_id": 2,
      "activity_name": "Breakfast",
      "start_time": "07:00",
      "end_time": "08:00",
      "notes": "Light breakfast"
    }
  ],
  "created_at": "2025-12-04T10:30:00",
  "updated_at": "2025-12-04T10:30:00"
}
```

**DataAccess Methods Needed:**
```java
Optional<ScheduleTemplate> findTemplateById(Long templateId);
List<TemplateScheduleActivity> findActivitiesByTemplateId(Long templateId);
```

**jOOQ Query for Activities:**
```java
public List<TemplateScheduleActivity> findActivitiesByTemplateId(Long templateId) {
    return dslContext
            .selectFrom(TEMPLATE_SCHEDULE_ACTIVITY)
            .where(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID.eq(templateId))
            .orderBy(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME.asc())
            .fetch(record -> TemplateScheduleActivity.builder()
                    .id(record.get(TEMPLATE_SCHEDULE_ACTIVITY.ID))
                    .templateId(record.get(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID))
                    .activityId(record.get(TEMPLATE_SCHEDULE_ACTIVITY.ACTIVITY_ID))
                    .startTime(record.get(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME))
                    .endTime(record.get(TEMPLATE_SCHEDULE_ACTIVITY.END_TIME))
                    .notes(record.get(TEMPLATE_SCHEDULE_ACTIVITY.NOTES))
                    .createdAt(record.get(TEMPLATE_SCHEDULE_ACTIVITY.CREATED_AT))
                    .updatedAt(record.get(TEMPLATE_SCHEDULE_ACTIVITY.UPDATED_AT))
                    .build()
            );
}
```

#### 3. PATCH Activate Template

**Business Rules:**
- Only ONE template can be active at a time
- When activating a template, deactivate all others first

**DataAccess Methods:**
```java
void deactivateAllTemplates();
boolean activateTemplate(Long templateId);
```

**jOOQ Queries:**
```java
@Override
public void deactivateAllTemplates() {
    dslContext
            .update(SCHEDULE_TEMPLATE)
            .set(SCHEDULE_TEMPLATE.IS_ACTIVE, false)
            .execute();
}

@Override
public boolean activateTemplate(Long templateId) {
    int updated = dslContext
            .update(SCHEDULE_TEMPLATE)
            .set(SCHEDULE_TEMPLATE.IS_ACTIVE, true)
            .where(SCHEDULE_TEMPLATE.TEMPLATE_ID.eq(templateId))
            .execute();

    return updated > 0;
}
```

**UseCase Logic:**
```java
@Transactional
public PatchActivateTemplateResponse handle(PatchActivateTemplateRequest request) {
    // Verify template exists
    ScheduleTemplate template = repository.findTemplateById(request.templateId())
            .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

    // Deactivate all templates
    repository.deactivateAllTemplates();

    // Activate this template
    repository.activateTemplate(request.templateId());

    log.info("Activated template: {}", request.templateId());

    return responseBuilder.build(template);
}
```

#### 4. GET Active Template

**Query:**
```java
public Optional<ScheduleTemplate> findActiveTemplate() {
    return dslContext
            .selectFrom(SCHEDULE_TEMPLATE)
            .where(SCHEDULE_TEMPLATE.IS_ACTIVE.eq(true))
            .fetchOptional(record -> /* map to ScheduleTemplate */);
}
```

---

## Phase 4: Template Activity Management

### Endpoints to Implement

| Method | Endpoint | Description | Files |
|--------|----------|-------------|-------|
| POST | `/api/admin/templates/{id}/activities` | Add activity to template | 7 files |
| PUT | `/api/admin/templates/{templateId}/activities/{activityId}` | Update activity in template | 7 files |
| DELETE | `/api/admin/templates/{templateId}/activities/{activityId}` | Remove activity from template | 4 files |
| PUT | `/api/admin/templates/{id}/activities/bulk` | Replace all activities | 7 files |

**Total: ~25 files**

### Special Considerations

#### PUT Bulk - Replace All Activities

**Use Case:** Admin wants to reorder entire schedule

**Request:**
```json
{
  "activities": [
    {
      "activity_id": 1,
      "start_time": "05:00",
      "end_time": "06:30",
      "notes": null
    },
    {
      "activity_id": 2,
      "start_time": "07:00",
      "end_time": "08:00",
      "notes": null
    }
  ]
}
```

**UseCase Logic:**
```java
@Transactional
public PutBulkTemplateActivitiesResponse execute(Long templateId, PutBulkTemplateActivitiesRequest request) {
    // Verify template exists
    repository.findTemplateById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

    // Delete all existing activities for this template
    repository.deleteAllActivitiesByTemplateId(templateId);

    // Insert new activities
    for (TemplateActivityDto activityDto : request.activities()) {
        TemplateScheduleActivity activity = /* build from DTO */;
        repository.addActivityToTemplate(activity);
    }

    return responseBuilder.build(templateId, request.activities().size());
}
```

---

## Phase 5: Override CRUD

### Endpoints to Implement

| Method | Endpoint | Description | Files |
|--------|----------|-------------|-------|
| POST | `/api/admin/overrides` | Create override with full schedule | 7 files |
| GET | `/api/admin/overrides` | List all overrides (paginated) | 7 files |
| GET | `/api/admin/overrides/{id}` | Get override with activities | 7 files |
| GET | `/api/admin/overrides/date/{date}` | Get override by date | 7 files |
| PUT | `/api/admin/overrides/{id}` | Replace override schedule | 7 files |
| DELETE | `/api/admin/overrides/{id}` | Delete override | 4 files |
| POST | `/api/admin/overrides/from-template` | Copy active template | 7 files |

**Total: ~46 files**

### Special Considerations

#### 1. POST Override - Create with Full Schedule

**Business Rules:**
- `override_date` must be UNIQUE
- Must provide FULL schedule (not partial)
- Activities can be marked as `is_cancelled`

**Request Structure:**
```json
{
  "override_date": "2025-12-15",
  "activities": [
    {
      "activity_id": 1,
      "start_time": "04:00",
      "end_time": "07:00",
      "is_cancelled": false,
      "notes": "Extended for Poya Day"
    },
    {
      "activity_id": 3,
      "start_time": "09:00",
      "end_time": "10:30",
      "is_cancelled": true,
      "notes": "Cancelled for special ceremony"
    }
  ]
}
```

**DataAccess Methods:**
```java
boolean existsByDate(LocalDate overrideDate);
ScheduleOverride createOverride(ScheduleOverride override);
OverrideActivity addActivityToOverride(OverrideActivity overrideActivity);
```

**Validation:**
```java
// Check if override already exists for this date
if (repository.existsByDate(request.overrideDate())) {
    throw new ConflictException("Override already exists for date: " + request.overrideDate());
}
```

#### 2. POST Override from Template

**Use Case:** Admin wants to create override by copying active template, then modify

**Request:**
```json
{
  "override_date": "2025-12-15"
}
```

**UseCase Logic:**
```java
@Transactional
public PostOverrideFromTemplateResponse execute(PostOverrideFromTemplateRequest request) {
    // Get active template
    ScheduleTemplate activeTemplate = repository.findActiveTemplate()
            .orElseThrow(() -> new ResourceNotFoundException("No active template found"));

    // Get template activities
    List<TemplateScheduleActivity> templateActivities =
            repository.findActivitiesByTemplateId(activeTemplate.templateId());

    // Create override
    ScheduleOverride override = ScheduleOverride.builder()
            .overrideDate(request.overrideDate())
            .build();

    ScheduleOverride createdOverride = repository.createOverride(override);

    // Copy activities from template to override
    for (TemplateScheduleActivity templateActivity : templateActivities) {
        OverrideActivity overrideActivity = OverrideActivity.builder()
                .overrideId(createdOverride.overrideId())
                .activityId(templateActivity.activityId())
                .startTime(templateActivity.startTime())
                .endTime(templateActivity.endTime())
                .notes(templateActivity.notes())
                .isCancelled(false)
                .build();

        repository.addActivityToOverride(overrideActivity);
    }

    return responseBuilder.build(createdOverride, templateActivities.size());
}
```

#### 3. GET Override by Date

**Query:**
```java
public Optional<ScheduleOverride> findByDate(LocalDate date) {
    return dslContext
            .selectFrom(SCHEDULE_OVERRIDE)
            .where(SCHEDULE_OVERRIDE.OVERRIDE_DATE.eq(date))
            .fetchOptional(record -> /* map to ScheduleOverride */);
}
```

---

## Phase 6: Override Activity Management

### Endpoints to Implement

| Method | Endpoint | Description | Files |
|--------|----------|-------------|-------|
| PUT | `/api/admin/overrides/{overrideId}/activities/{activityId}` | Update activity in override | 7 files |
| PATCH | `/api/admin/overrides/{overrideId}/activities/{activityId}/cancel` | Mark activity as cancelled | 4 files |
| PATCH | `/api/admin/overrides/{overrideId}/activities/{activityId}/restore` | Unmark activity as cancelled | 4 files |

**Total: ~15 files**

### Special Considerations

#### PATCH Cancel Activity

**Request:** None (just path variables)

**UseCase Logic:**
```java
@Transactional
public PatchCancelOverrideActivityResponse execute(Long overrideId, Long activityId) {
    // Find the override activity record
    OverrideActivity activity = repository.findOverrideActivity(overrideId, activityId)
            .orElseThrow(() -> new ResourceNotFoundException("Activity not found in override"));

    // Update is_cancelled flag
    repository.updateCancelledStatus(activity.id(), true);

    return PatchCancelOverrideActivityResponse.success(overrideId, activityId);
}
```

**jOOQ Query:**
```java
public void updateCancelledStatus(Long id, boolean cancelled) {
    dslContext
            .update(OVERRIDE_ACTIVITY)
            .set(OVERRIDE_ACTIVITY.IS_CANCELLED, cancelled)
            .where(OVERRIDE_ACTIVITY.ID.eq(id))
            .execute();
}
```

---

## Phase 7: Public Schedule Preview API

### Endpoint to Implement

| Method | Endpoint | Description | Files |
|--------|----------|-------------|-------|
| GET | `/api/schedule/preview?date=YYYY-MM-DD` | Get schedule for specific date | 7 files |

**Total: ~7 files**

### Implementation

**Location:** `src/main/java/com/isipathana/meditationcenter/rest/schedule/preview/`

**Controller:** `ScheduleController.java`

**Security:** Public endpoint (no auth required)

**Request:**
```
GET /api/schedule/preview?date=2025-12-15
```

**Response Structure:**
```json
{
  "date": "2025-12-15",
  "is_override": true,
  "source": "override",
  "activities": [
    {
      "activity_id": 1,
      "title": "Morning Meditation",
      "description": "Silent meditation session",
      "start_time": "04:00",
      "end_time": "07:00",
      "is_cancelled": false,
      "notes": "Extended for Poya Day",
      "media_url": "https://..."
    },
    {
      "activity_id": 3,
      "title": "Dharma Talk",
      "description": "Teaching session",
      "start_time": "09:00",
      "end_time": "10:30",
      "is_cancelled": true,
      "notes": "Cancelled for special ceremony",
      "media_url": null
    }
  ]
}
```

### UseCase Logic (CRITICAL)

```java
@Transactional(readOnly = true)
public GetSchedulePreviewResponse handle(GetSchedulePreviewRequest request) {
    log.info("Fetching schedule for date: {}", request.date());

    // Step 1: Check if override exists for this date
    Optional<ScheduleOverride> override = repository.findOverrideByDate(request.date());

    if (override.isPresent()) {
        // Use override schedule
        log.info("Override found for date: {}", request.date());

        List<OverrideActivity> overrideActivities =
                repository.findActivitiesByOverrideId(override.get().overrideId());

        // Get activity details (join with activity table)
        List<ActivityWithDetails> activities =
                repository.getActivityDetailsForOverride(override.get().overrideId());

        return responseBuilder.buildFromOverride(request.date(), activities);
    }

    // Step 2: No override, use active template
    log.info("No override found, using active template");

    ScheduleTemplate activeTemplate = repository.findActiveTemplate()
            .orElseThrow(() -> new ResourceNotFoundException("No active template found"));

    List<TemplateScheduleActivity> templateActivities =
            repository.findActivitiesByTemplateId(activeTemplate.templateId());

    // Get activity details (join with activity table)
    List<ActivityWithDetails> activities =
            repository.getActivityDetailsForTemplate(activeTemplate.templateId());

    return responseBuilder.buildFromTemplate(request.date(), activities);
}
```

### DataAccess Methods Needed

```java
public interface GetSchedulePreviewDataAccess {
    Optional<ScheduleOverride> findOverrideByDate(LocalDate date);
    Optional<ScheduleTemplate> findActiveTemplate();
    List<ActivityWithDetails> getActivityDetailsForOverride(Long overrideId);
    List<ActivityWithDetails> getActivityDetailsForTemplate(Long templateId);
}
```

### jOOQ Query with JOIN

```java
public List<ActivityWithDetails> getActivityDetailsForTemplate(Long templateId) {
    return dslContext
            .select(
                    ACTIVITY.ACTIVITY_ID,
                    ACTIVITY.TITLE,
                    ACTIVITY.DESCRIPTION,
                    ACTIVITY.MEDIA_URL,
                    TEMPLATE_SCHEDULE_ACTIVITY.START_TIME,
                    TEMPLATE_SCHEDULE_ACTIVITY.END_TIME,
                    TEMPLATE_SCHEDULE_ACTIVITY.NOTES
            )
            .from(TEMPLATE_SCHEDULE_ACTIVITY)
            .join(ACTIVITY)
                .on(TEMPLATE_SCHEDULE_ACTIVITY.ACTIVITY_ID.eq(ACTIVITY.ACTIVITY_ID))
            .where(TEMPLATE_SCHEDULE_ACTIVITY.TEMPLATE_ID.eq(templateId))
            .orderBy(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME.asc())
            .fetch(record -> ActivityWithDetails.builder()
                    .activityId(record.get(ACTIVITY.ACTIVITY_ID))
                    .title(record.get(ACTIVITY.TITLE))
                    .description(record.get(ACTIVITY.DESCRIPTION))
                    .mediaUrl(record.get(ACTIVITY.MEDIA_URL))
                    .startTime(record.get(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME))
                    .endTime(record.get(TEMPLATE_SCHEDULE_ACTIVITY.END_TIME))
                    .notes(record.get(TEMPLATE_SCHEDULE_ACTIVITY.NOTES))
                    .isCancelled(false) // Templates don't have cancelled flag
                    .build()
            );
}

public List<ActivityWithDetails> getActivityDetailsForOverride(Long overrideId) {
    return dslContext
            .select(
                    ACTIVITY.ACTIVITY_ID,
                    ACTIVITY.TITLE,
                    ACTIVITY.DESCRIPTION,
                    ACTIVITY.MEDIA_URL,
                    OVERRIDE_ACTIVITY.START_TIME,
                    OVERRIDE_ACTIVITY.END_TIME,
                    OVERRIDE_ACTIVITY.NOTES,
                    OVERRIDE_ACTIVITY.IS_CANCELLED
            )
            .from(OVERRIDE_ACTIVITY)
            .join(ACTIVITY)
                .on(OVERRIDE_ACTIVITY.ACTIVITY_ID.eq(ACTIVITY.ACTIVITY_ID))
            .where(OVERRIDE_ACTIVITY.OVERRIDE_ID.eq(overrideId))
            .orderBy(OVERRIDE_ACTIVITY.START_TIME.asc())
            .fetch(record -> ActivityWithDetails.builder()
                    .activityId(record.get(ACTIVITY.ACTIVITY_ID))
                    .title(record.get(ACTIVITY.TITLE))
                    .description(record.get(ACTIVITY.DESCRIPTION))
                    .mediaUrl(record.get(ACTIVITY.MEDIA_URL))
                    .startTime(record.get(OVERRIDE_ACTIVITY.START_TIME))
                    .endTime(record.get(OVERRIDE_ACTIVITY.END_TIME))
                    .notes(record.get(OVERRIDE_ACTIVITY.NOTES))
                    .isCancelled(record.get(OVERRIDE_ACTIVITY.IS_CANCELLED))
                    .build()
            );
}
```

### ActivityWithDetails Record

```java
package com.isipathana.meditationcenter.rest.schedule.preview;

import lombok.Builder;
import java.time.LocalTime;

/**
 * Combined activity details for schedule preview.
 *
 * @author Sathira Basnayake
 */
@Builder
public record ActivityWithDetails(
    Long activityId,
    String title,
    String description,
    String mediaUrl,
    LocalTime startTime,
    LocalTime endTime,
    String notes,
    Boolean isCancelled
) {}
```

---

## Phase 8: Security Configuration

### Update Role.java with Permissions

**Location:** `src/main/java/com/isipathana/meditationcenter/security/Role.java`

Add new permissions:

```java
public enum Role {
    USER(Set.of(
        Permission.VIEW_PROGRAMS,
        Permission.VIEW_EVENTS,
        Permission.VIEW_SCHEDULE  // New
    )),

    INSTRUCTOR(Set.of(
        Permission.VIEW_PROGRAMS,
        Permission.VIEW_EVENTS,
        Permission.VIEW_SCHEDULE,  // New
        Permission.VIEW_ACTIVITIES,  // New
        Permission.VIEW_TEMPLATES  // New
    )),

    ADMIN(Set.of(
        // Activity permissions
        Permission.CREATE_ACTIVITY,
        Permission.VIEW_ACTIVITIES,
        Permission.UPDATE_ACTIVITY,
        Permission.DELETE_ACTIVITY,

        // Template permissions
        Permission.CREATE_TEMPLATE,
        Permission.VIEW_TEMPLATES,
        Permission.UPDATE_TEMPLATE,
        Permission.DELETE_TEMPLATE,
        Permission.ACTIVATE_TEMPLATE,

        // Override permissions
        Permission.CREATE_OVERRIDE,
        Permission.VIEW_OVERRIDES,
        Permission.UPDATE_OVERRIDE,
        Permission.DELETE_OVERRIDE,

        // Schedule permissions
        Permission.VIEW_SCHEDULE
    ));
}
```

### Update Permission.java

```java
public enum Permission {
    // Activity permissions
    CREATE_ACTIVITY("CREATE_ACTIVITY"),
    VIEW_ACTIVITIES("VIEW_ACTIVITIES"),
    UPDATE_ACTIVITY("UPDATE_ACTIVITY"),
    DELETE_ACTIVITY("DELETE_ACTIVITY"),

    // Template permissions
    CREATE_TEMPLATE("CREATE_TEMPLATE"),
    VIEW_TEMPLATES("VIEW_TEMPLATES"),
    UPDATE_TEMPLATE("UPDATE_TEMPLATE"),
    DELETE_TEMPLATE("DELETE_TEMPLATE"),
    ACTIVATE_TEMPLATE("ACTIVATE_TEMPLATE"),

    // Override permissions
    CREATE_OVERRIDE("CREATE_OVERRIDE"),
    VIEW_OVERRIDES("VIEW_OVERRIDES"),
    UPDATE_OVERRIDE("UPDATE_OVERRIDE"),
    DELETE_OVERRIDE("DELETE_OVERRIDE"),

    // Schedule permissions
    VIEW_SCHEDULE("VIEW_SCHEDULE");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
```

### Update SecurityConfig.java

Add public endpoint for schedule preview:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers(HttpMethod.POST, EndPoints.Auth.FULL_PATH).permitAll()
            .requestMatchers(HttpMethod.GET, EndPoints.Event.FULL_PATH).permitAll()
            .requestMatchers(HttpMethod.GET, EndPoints.Schedule.PREVIEW).permitAll()  // NEW

            // Protected endpoints
            .requestMatchers(EndPoints.Admin.Activity.FULL_PATH).authenticated()
            .requestMatchers(EndPoints.Admin.Template.FULL_PATH).authenticated()  // NEW
            .requestMatchers(EndPoints.API + "/**").authenticated()

            .anyRequest().permitAll()
        )
        // ... rest of configuration
}
```

Add Schedule endpoints to EndPoints.java:

```java
public static final class Schedule {
    private Schedule() {}

    public static final String BASE = API + "/schedule";
    public static final String PREVIEW = BASE + "/preview";
}
```

---

## Testing Checklist

### Activity CRUD (✅ Complete)

- [ ] POST /api/admin/activities - Creates activity
- [ ] GET /api/admin/activities - Lists activities with pagination
- [ ] GET /api/admin/activities/{id} - Returns single activity
- [ ] PATCH /api/admin/activities/{id} - Updates activity
- [ ] DELETE /api/admin/activities/{id} - Deletes activity
- [ ] All endpoints require ADMIN role
- [ ] 404 returned for non-existent IDs
- [ ] Validation errors return 400

### Template CRUD

- [ ] POST /api/admin/templates - Creates template with activities
- [ ] Validates end_time > start_time
- [ ] New templates default to is_active = false
- [ ] GET /api/admin/templates - Lists templates
- [ ] GET /api/admin/templates/active - Returns active template
- [ ] GET /api/admin/templates/{id} - Returns template with activities
- [ ] Activities sorted by start_time
- [ ] PUT /api/admin/templates/{id} - Updates name/description
- [ ] PATCH /api/admin/templates/{id}/activate - Activates template
- [ ] Deactivates other templates
- [ ] Only one template active at a time
- [ ] DELETE /api/admin/templates/{id} - Deletes template
- [ ] Cascade deletes template_schedule_activity

### Template Activity Management

- [ ] POST /api/admin/templates/{id}/activities - Adds activity
- [ ] PUT /api/admin/templates/{templateId}/activities/{activityId} - Updates
- [ ] DELETE /api/admin/templates/{templateId}/activities/{activityId} - Removes
- [ ] PUT /api/admin/templates/{id}/activities/bulk - Replaces all

### Override CRUD

- [ ] POST /api/admin/overrides - Creates override
- [ ] Validates unique override_date
- [ ] GET /api/admin/overrides - Lists overrides
- [ ] GET /api/admin/overrides/{id} - Returns override with activities
- [ ] GET /api/admin/overrides/date/{date} - Returns by date
- [ ] PUT /api/admin/overrides/{id} - Updates override
- [ ] DELETE /api/admin/overrides/{id} - Deletes override
- [ ] POST /api/admin/overrides/from-template - Copies template

### Override Activity Management

- [ ] PUT /api/admin/overrides/{overrideId}/activities/{activityId} - Updates
- [ ] PATCH .../cancel - Marks as cancelled
- [ ] PATCH .../restore - Unmarks as cancelled

### Schedule Preview (Public)

- [ ] GET /api/schedule/preview?date=YYYY-MM-DD
- [ ] Returns override if exists for date
- [ ] Returns template if no override
- [ ] Returns 404 if no template exists
- [ ] Includes activity details (JOIN)
- [ ] Shows is_cancelled flag
- [ ] Sorted by start_time
- [ ] No authentication required

### Security

- [ ] All admin endpoints require authentication
- [ ] Correct permissions checked
- [ ] Public preview endpoint accessible
- [ ] 401 for unauthenticated requests
- [ ] 403 for unauthorized requests

---

## Common Pitfalls

### 1. Forgetting @Transactional

**Problem:** Changes not committed to database

**Solution:** Always add `@Transactional` to UseCases that modify data

```java
@Transactional  // ← Don't forget!
public PostTemplateResponse execute(PostTemplateRequest request) {
    // ...
}
```

### 2. Not Ordering Activities by Time

**Problem:** Activities returned in random order

**Solution:** Always `ORDER BY start_time ASC`

```java
.orderBy(TEMPLATE_SCHEDULE_ACTIVITY.START_TIME.asc())
```

### 3. Forgetting to Deactivate Other Templates

**Problem:** Multiple templates active simultaneously

**Solution:** Deactivate all before activating one

```java
repository.deactivateAllTemplates();
repository.activateTemplate(templateId);
```

### 4. Not Validating end_time > start_time

**Problem:** Invalid time ranges accepted

**Solution:** Add validation in UseCase

```java
if (!activity.endTime().isAfter(activity.startTime())) {
    throw new ValidationException("End time must be after start time");
}
```

### 5. Forgetting Cascade Delete

**Problem:** Orphaned template_schedule_activity records

**Solution:** Database already has ON DELETE CASCADE

### 6. Not Checking for Duplicate Override Dates

**Problem:** Constraint violation exception

**Solution:** Check before creating

```java
if (repository.existsByDate(request.overrideDate())) {
    throw new ConflictException("Override already exists for date");
}
```

### 7. Missing JOIN in Schedule Preview

**Problem:** Only have activity IDs, not names/descriptions

**Solution:** Use JOIN query to get full activity details

---

## File Count Summary

| Phase | Endpoints | Files | Status |
|-------|-----------|-------|--------|
| 1-2 | Activity CRUD (5) | 34 | ✅ Complete |
| 3 | Template CRUD (7) | ~45 | 🔜 Pending |
| 4 | Template Activities (4) | ~25 | 🔜 Pending |
| 5 | Override CRUD (7) | ~46 | 🔜 Pending |
| 6 | Override Activities (3) | ~15 | 🔜 Pending |
| 7 | Schedule Preview (1) | ~7 | 🔜 Pending |
| 8 | Security Config | 3 | 🔜 Pending |
| **Total** | **27 endpoints** | **~175 files** | **19% Complete** |

---

## Quick Start: Next Steps

1. **Implement Template CRUD** (Phase 3)
   - Start with POST (most complex)
   - Reference POST Activity for pattern
   - Add nested DTO for activities
   - Implement bulk operations

2. **Implement Override CRUD** (Phase 5)
   - Similar to Template CRUD
   - Add "from-template" helper
   - Handle is_cancelled flag

3. **Implement Schedule Preview** (Phase 7)
   - CRITICAL endpoint - this is what users see
   - Implement override → template fallback logic
   - Use JOIN queries for activity details

4. **Update Security** (Phase 8)
   - Add permissions to Role.java
   - Update SecurityConfig.java

---

## Additional Resources

### Reference Implementations

All Activity endpoints are fully implemented and follow the exact pattern:

- **Simple GET**: `GetSingleActivityUseCase.java`
- **Paginated GET**: `GetActivitiesUseCase.java`
- **POST**: `PostActivityUseCase.java`
- **PATCH**: `PatchActivityUseCase.java`
- **DELETE**: `DeleteActivityUseCase.java`

### Database Schema

See: `src/main/resources/db/migration/README.md`

### Architecture Pattern

See: `MeditationCenter/CLAUDE.md`

### Daily Schedule Logic

See: `MeditationCenter/dailySchedule.md`

---

## Questions?

If you encounter issues:

1. Check Activity endpoints as reference
2. Verify jOOQ table classes generated
3. Check database constraints
4. Ensure @Transactional on write operations
5. Validate request DTOs properly
6. Use proper HTTP status codes (201 for POST, 404 for not found)

---

**End of Implementation Guide**

This guide provides everything needed to complete Phases 3-8 following the established patterns. The Activity CRUD implementation serves as a complete working example for all other endpoints.
