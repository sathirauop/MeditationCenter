# API Endpoints Documentation

This document lists all implemented endpoints for the Meditation Center Daily Schedule Management System.

**Base URL:** `http://localhost:8080/api`

**Authentication:** All admin endpoints require JWT Bearer token in the `Authorization` header.

---

## Activity Management Endpoints

### 1. Create Activity
**POST** `/api/admin/activities`

**Permission Required:** `ADMIN` role + `CREATE_ACTIVITY` permission

**Request Body:**
```json
{
  "title": "Morning Meditation",
  "description": "Guided morning meditation session focusing on breath awareness",
  "media_url": "https://example.com/meditation-video.mp4"
}
```

**Response:** `201 CREATED`
```json
{
  "activity_id": 1,
  "title": "Morning Meditation",
  "description": "Guided morning meditation session focusing on breath awareness",
  "media_url": "https://example.com/meditation-video.mp4",
  "created_at": "2025-12-04T10:30:00"
}
```

---

### 2. Get All Activities
**GET** `/api/admin/activities?limit=20&offset=0`

**Permission Required:** `ADMIN` role + `VIEW_ACTIVITIES` permission

**Query Parameters:**
- `limit` (optional, default: 20, max: 100) - Number of results per page
- `offset` (optional, default: 0) - Pagination offset

**Response:** `200 OK`
```json
{
  "data": [
    {
      "activity_id": 1,
      "title": "Morning Meditation",
      "description": "Guided morning meditation session",
      "media_url": "https://example.com/meditation-video.mp4",
      "created_at": "2025-12-04T10:30:00",
      "updated_at": "2025-12-04T10:30:00"
    }
  ],
  "current_offset": 0,
  "max_offset": 5
}
```

---

### 3. Get Single Activity
**GET** `/api/admin/activities/{id}`

**Permission Required:** `ADMIN` role + `VIEW_ACTIVITIES` permission

**Response:** `200 OK`
```json
{
  "activity_id": 1,
  "title": "Morning Meditation",
  "description": "Guided morning meditation session",
  "media_url": "https://example.com/meditation-video.mp4",
  "created_at": "2025-12-04T10:30:00",
  "updated_at": "2025-12-04T10:30:00"
}
```

---

### 4. Update Activity
**PATCH** `/api/admin/activities/{id}`

**Permission Required:** `ADMIN` role + `UPDATE_ACTIVITY` permission

**Request Body:** (All fields optional - partial update)
```json
{
  "title": "Advanced Morning Meditation",
  "description": "Advanced guided morning meditation for experienced practitioners",
  "media_url": "https://example.com/advanced-meditation.mp4"
}
```

**Response:** `200 OK`
```json
{
  "activity_id": 1,
  "title": "Advanced Morning Meditation",
  "description": "Advanced guided morning meditation for experienced practitioners",
  "media_url": "https://example.com/advanced-meditation.mp4",
  "updated_at": "2025-12-04T11:00:00"
}
```

---

### 5. Delete Activity
**DELETE** `/api/admin/activities/{id}`

**Permission Required:** `ADMIN` role + `DELETE_ACTIVITY` permission

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Activity 'Morning Meditation' (ID: 1) deleted successfully",
  "activity_id": 1,
  "activity_title": "Morning Meditation"
}
```

---

## Template Management Endpoints

### 6. Create Template
**POST** `/api/admin/templates`

**Permission Required:** `ADMIN` role + `CREATE_TEMPLATE` permission

**Request Body:**
```json
{
  "name": "Weekday Schedule",
  "description": "Standard Monday to Friday daily schedule",
  "activities": [
    {
      "activityId": 1,
      "startTime": "05:00",
      "endTime": "06:00",
      "notes": "Morning meditation session"
    },
    {
      "activityId": 2,
      "startTime": "06:00",
      "endTime": "07:00",
      "notes": "Breakfast and community time"
    },
    {
      "activityId": 3,
      "startTime": "09:00",
      "endTime": "10:30",
      "notes": "Dharma talk"
    }
  ]
}
```

**Response:** `201 CREATED`
```json
{
  "template_id": 1,
  "name": "Weekday Schedule",
  "description": "Standard Monday to Friday daily schedule",
  "is_active": false,
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "start_time": "05:00",
      "end_time": "06:00",
      "notes": "Morning meditation session"
    },
    {
      "activity_id": 2,
      "activity_title": "Breakfast",
      "start_time": "06:00",
      "end_time": "07:00",
      "notes": "Breakfast and community time"
    },
    {
      "activity_id": 3,
      "activity_title": "Dharma Talk",
      "start_time": "09:00",
      "end_time": "10:30",
      "notes": "Dharma talk"
    }
  ],
  "created_at": "2025-12-04T10:30:00"
}
```

---

### 7. Get All Templates
**GET** `/api/admin/templates?limit=20&offset=0`

**Permission Required:** `ADMIN` role + `VIEW_TEMPLATES` permission

**Query Parameters:**
- `limit` (optional, default: 20, max: 100)
- `offset` (optional, default: 0)

**Response:** `200 OK`
```json
{
  "data": [
    {
      "template_id": 1,
      "name": "Weekday Schedule",
      "description": "Standard Monday to Friday daily schedule",
      "is_active": true,
      "activity_count": 5,
      "created_at": "2025-12-04T10:30:00",
      "updated_at": "2025-12-04T10:30:00"
    }
  ],
  "current_offset": 0,
  "max_offset": 2
}
```

---

### 8. Get Active Template
**GET** `/api/admin/templates/active`

**Permission Required:** `ADMIN` role + `VIEW_TEMPLATES` permission

**Response:** `200 OK`
```json
{
  "template_id": 1,
  "name": "Weekday Schedule",
  "description": "Standard Monday to Friday daily schedule",
  "is_active": true,
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "start_time": "05:00",
      "end_time": "06:00",
      "notes": "Morning meditation session"
    }
  ],
  "created_at": "2025-12-04T10:30:00",
  "updated_at": "2025-12-04T10:30:00"
}
```

---

### 9. Get Template by ID
**GET** `/api/admin/templates/{id}`

**Permission Required:** `ADMIN` role + `VIEW_TEMPLATES` permission

**Response:** `200 OK`
```json
{
  "template_id": 1,
  "name": "Weekday Schedule",
  "description": "Standard Monday to Friday daily schedule",
  "is_active": true,
  "activities": [
    {
      "template_activity_id": 1,
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "activity_description": "Guided morning meditation session",
      "start_time": "05:00",
      "end_time": "06:00",
      "notes": "Morning meditation session"
    }
  ],
  "created_at": "2025-12-04T10:30:00",
  "updated_at": "2025-12-04T10:30:00"
}
```

---

### 10. Update Template (Full Replacement)
**PUT** `/api/admin/templates/{id}`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Request Body:**
```json
{
  "name": "Updated Weekday Schedule",
  "description": "Modified schedule with new activities",
  "activities": [
    {
      "activityId": 1,
      "startTime": "05:30",
      "endTime": "06:30",
      "notes": "Extended morning meditation"
    },
    {
      "activityId": 4,
      "startTime": "07:00",
      "endTime": "08:00",
      "notes": "Yoga session"
    }
  ]
}
```

**Response:** `200 OK`
```json
{
  "template_id": 1,
  "name": "Updated Weekday Schedule",
  "description": "Modified schedule with new activities",
  "is_active": true,
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "start_time": "05:30",
      "end_time": "06:30",
      "notes": "Extended morning meditation"
    },
    {
      "activity_id": 4,
      "activity_title": "Yoga",
      "start_time": "07:00",
      "end_time": "08:00",
      "notes": "Yoga session"
    }
  ],
  "updated_at": "2025-12-04T11:00:00"
}
```

---

### 11. Activate Template
**PATCH** `/api/admin/templates/{id}/activate`

**Permission Required:** `ADMIN` role + `ACTIVATE_TEMPLATE` permission

**Note:** Only ONE template can be active at a time. This endpoint automatically deactivates all other templates.

**Response:** `200 OK`
```json
{
  "template_id": 1,
  "name": "Weekday Schedule",
  "is_active": true,
  "previous_active_template_id": 2,
  "updated_at": "2025-12-04T11:00:00",
  "message": "Template 'Weekday Schedule' activated. Previous template (ID: 2) deactivated."
}
```

---

### 12. Delete Template
**DELETE** `/api/admin/templates/{id}`

**Permission Required:** `ADMIN` role + `DELETE_TEMPLATE` permission

**Note:** Deleting a template automatically deletes all associated template activities (CASCADE).

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Template 'Weekday Schedule' (ID: 1) deleted successfully",
  "template_id": 1,
  "template_name": "Weekday Schedule"
}
```

---

## Template Activity Management Endpoints

### 13. Add Activity to Template
**POST** `/api/admin/templates/{id}/activities`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Request Body:**
```json
{
  "activityId": 5,
  "startTime": "14:00",
  "endTime": "15:00",
  "notes": "Afternoon walking meditation"
}
```

**Response:** `201 CREATED`
```json
{
  "template_activity_id": 10,
  "template_id": 1,
  "activity_id": 5,
  "activity_title": "Walking Meditation",
  "start_time": "14:00",
  "end_time": "15:00",
  "notes": "Afternoon walking meditation",
  "created_at": "2025-12-04T11:00:00"
}
```

---

### 14. Update Template Activity
**PUT** `/api/admin/templates/{templateId}/activities/{activityId}`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Note:** This updates the time and notes for an activity within a template. The `activityId` in the URL is the `template_activity_id`, NOT the activity ID.

**Request Body:**
```json
{
  "startTime": "14:30",
  "endTime": "15:30",
  "notes": "Extended afternoon walking meditation"
}
```

**Response:** `200 OK`
```json
{
  "template_activity_id": 10,
  "template_id": 1,
  "activity_id": 5,
  "activity_title": "Walking Meditation",
  "start_time": "14:30",
  "end_time": "15:30",
  "notes": "Extended afternoon walking meditation",
  "updated_at": "2025-12-04T11:30:00"
}
```

---

### 15. Remove Activity from Template
**DELETE** `/api/admin/templates/{templateId}/activities/{activityId}`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Note:** The `activityId` in the URL is the `template_activity_id`, NOT the activity ID.

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Activity 'Walking Meditation' removed from template successfully",
  "template_activity_id": 10,
  "template_id": 1,
  "activity_title": "Walking Meditation"
}
```

---

### 16. Bulk Update Template Activities
**PUT** `/api/admin/templates/{id}/activities/bulk`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Note:** This replaces ALL activities in the template with the provided list. All existing activities will be deleted and replaced.

**Request Body:**
```json
{
  "activities": [
    {
      "activityId": 1,
      "startTime": "05:00",
      "endTime": "06:00",
      "notes": "Morning meditation"
    },
    {
      "activityId": 2,
      "startTime": "06:00",
      "endTime": "07:00",
      "notes": "Breakfast"
    },
    {
      "activityId": 3,
      "startTime": "09:00",
      "endTime": "10:30",
      "notes": "Dharma talk"
    },
    {
      "activityId": 4,
      "startTime": "14:00",
      "endTime": "15:00",
      "notes": "Yoga session"
    }
  ]
}
```

**Response:** `200 OK`
```json
{
  "template_id": 1,
  "template_name": "Weekday Schedule",
  "activities_count": 4,
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "start_time": "05:00",
      "end_time": "06:00",
      "notes": "Morning meditation"
    },
    {
      "activity_id": 2,
      "activity_title": "Breakfast",
      "start_time": "06:00",
      "end_time": "07:00",
      "notes": "Breakfast"
    },
    {
      "activity_id": 3,
      "activity_title": "Dharma Talk",
      "start_time": "09:00",
      "end_time": "10:30",
      "notes": "Dharma talk"
    },
    {
      "activity_id": 4,
      "activity_title": "Yoga",
      "start_time": "14:00",
      "end_time": "15:00",
      "notes": "Yoga session"
    }
  ],
  "message": "Successfully updated 4 activities for template 'Weekday Schedule'"
}
```

---

## Testing Workflow Example

### Step 1: Create Activities
```bash
# Create Activity 1
POST /api/admin/activities
{
  "title": "Morning Meditation",
  "description": "Guided morning meditation",
  "media_url": "https://example.com/video1.mp4"
}

# Create Activity 2
POST /api/admin/activities
{
  "title": "Breakfast",
  "description": "Community breakfast time",
  "media_url": null
}

# Create Activity 3
POST /api/admin/activities
{
  "title": "Dharma Talk",
  "description": "Daily wisdom teachings",
  "media_url": "https://example.com/video2.mp4"
}
```

### Step 2: Create Template with Activities
```bash
POST /api/admin/templates
{
  "name": "Weekday Schedule",
  "description": "Monday to Friday schedule",
  "activities": [
    {
      "activityId": 1,
      "startTime": "05:00",
      "endTime": "06:00",
      "notes": "Morning session"
    },
    {
      "activityId": 2,
      "startTime": "06:00",
      "endTime": "07:00",
      "notes": "Breakfast time"
    },
    {
      "activityId": 3,
      "startTime": "09:00",
      "endTime": "10:30",
      "notes": "Daily talk"
    }
  ]
}
```

### Step 3: Activate Template
```bash
PATCH /api/admin/templates/1/activate
```

### Step 4: Get Active Template
```bash
GET /api/admin/templates/active
```

### Step 5: Add Single Activity
```bash
POST /api/admin/templates/1/activities
{
  "activityId": 4,
  "startTime": "14:00",
  "endTime": "15:00",
  "notes": "Afternoon meditation"
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": [
    "Activity ID is required",
    "Start time is required"
  ]
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Unauthorized - Invalid or missing token"
}
```

### 403 Forbidden
```json
{
  "status": 403,
  "message": "Access denied - Insufficient permissions"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Template not found with ID: 999"
}
```

---

## Notes

1. **Time Format:** All times use 24-hour format `HH:mm` (e.g., "05:00", "14:30")
2. **Date Format:** All dates use ISO 8601 format `yyyy-MM-dd'T'HH:mm:ss`
3. **Authentication:** Include JWT token in header: `Authorization: Bearer <token>`
4. **Only One Active Template:** The system enforces that only one template can be active at a time
5. **CASCADE Deletion:** Deleting a template automatically deletes all its activities
6. **Activity ID vs Template Activity ID:**
   - `activity_id` refers to the reusable activity definition
   - `template_activity_id` refers to a specific instance of an activity within a template

---

## Postman Collection Tips

1. Create environment variables:
   - `base_url`: `http://localhost:8080`
   - `token`: Your JWT Bearer token
   - `activity_id_1`: ID of first created activity
   - `template_id`: ID of created template

2. Set Authorization header for all admin requests:
   - Type: Bearer Token
   - Token: `{{token}}`

3. Save response IDs to environment variables for chaining requests
