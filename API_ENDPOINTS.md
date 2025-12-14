# API Endpoints Documentation

This document lists all implemented endpoints for the Meditation Center Daily Schedule Management System.

**Base URL:** `http://localhost:8080/api`

**Authentication:** All admin endpoints require JWT Bearer token in the `Authorization` header.

---

## Table of Contents

1. [Authentication Endpoints](#authentication-endpoints)
2. [Event Endpoints (Public)](#event-endpoints-public)
3. [Program Endpoints (Public)](#program-endpoints-public)
4. [Schedule Endpoints (Public)](#schedule-endpoints-public)
5. [Book Endpoints (Public)](#book-endpoints-public)
6. [Admin Event Management](#admin-event-management)
7. [Admin Program Management](#admin-program-management)
8. [Admin Activity Management](#admin-activity-management)
9. [Admin Template Management](#admin-template-management)
10. [Admin Override Management](#admin-override-management)
11. [Admin Book Management](#admin-book-management)
12. [Admin User Management](#admin-user-management)
13. [Utility Endpoints (Development Only)](#utility-endpoints-development-only)

---

## Authentication Endpoints

### 1. Register User
**POST** `/api/auth/register`

**Permission Required:** None (Public endpoint)

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123",
  "name": "John Doe",
  "mobileNumber": "+94771234567"
}
```

**Response:** `201 CREATED`
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900000
}
```

---

### 2. Login User
**POST** `/api/auth/login`

**Permission Required:** None (Public endpoint)

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123"
}
```

**Response:** `200 OK`
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900000
}
```

---

### 3. Refresh Access Token
**POST** `/api/auth/refresh`

**Permission Required:** None (Public endpoint)

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `200 OK`
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900000
}
```

**Note:** Refresh endpoint returns only a new access token, not a new refresh token.

---

### 4. Logout User
**POST** `/api/auth/logout`

**Permission Required:** None (Public endpoint)

**Response:** `200 OK`

**Note:** Since JWT is stateless, logout is primarily a client-side operation. Client should delete stored tokens. This endpoint is a placeholder for future token blacklist implementation.

---

## Event Endpoints (Public)

### 5. Get All Events
**GET** `/api/event?limit=20&offset=0`

**Permission Required:** None (Public endpoint)

**Query Parameters:**
- `limit` (optional, default: 20, max: 100) - Number of results per page
- `offset` (optional, default: 0) - Pagination offset

**Response:** `200 OK`
```json
{
  "data": [
    {
      "event_id": 1,
      "name": "Full Moon Meditation",
      "description": "Special full moon meditation ceremony",
      "event_date": "2025-12-15",
      "start_time": "18:00",
      "end_time": "20:00",
      "location": "Main Hall",
      "cover_image_url": "https://presigned-url.cloudflare.com/...",
      "gallery_image_urls": [
        "https://presigned-url.cloudflare.com/...",
        "https://presigned-url.cloudflare.com/..."
      ]
    }
  ],
  "currentOffset": 0,
  "maxOffset": 5
}
```

**Note:** Image URLs are presigned URLs with 5-minute expiry for secure temporary access.

---

### 6. Get Single Event
**GET** `/api/event/{id}`

**Permission Required:** None (Public endpoint)

**Response:** `200 OK`
```json
{
  "event_id": 1,
  "name": "Full Moon Meditation",
  "description": "Special full moon meditation ceremony",
  "event_date": "2025-12-15",
  "start_time": "18:00",
  "end_time": "20:00",
  "location": "Main Hall",
  "cover_image_url": "https://presigned-url.cloudflare.com/...",
  "gallery_image_urls": [
    "https://presigned-url.cloudflare.com/...",
    "https://presigned-url.cloudflare.com/..."
  ]
}
```

---

## Program Endpoints (Public)

### 7. Get Program by ID
**GET** `/api/programs/{id}`

**Permission Required:** None (Public endpoint)

**Path Parameters:**
- `id` (Long) - Program ID

**Response:** `200 OK`
```json
{
  "meditation_program_id": 1,
  "name": "Vipassana Meditation Retreat",
  "description": "10-day intensive Vipassana meditation retreat focused on developing mindfulness and insight...",
  "max_seats": 50,
  "cover_image_url": "https://r2.meditation-center.com/programs/1/cover/uuid.jpg?presigned=...",
  "gallery_image_urls": [
    "https://r2.meditation-center.com/programs/1/gallery/uuid1.jpg?presigned=...",
    "https://r2.meditation-center.com/programs/1/gallery/uuid2.jpg?presigned=..."
  ]
}
```

**Error Responses:**
- `404 NOT FOUND` - Program not found or inactive
```json
{
  "timestamp": "2025-12-13T08:45:50.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Active meditation program not found with ID: 999",
  "path": "/api/programs/999"
}
```

**Important Notes:**
- Only returns programs where `is_active = true`
- Image URLs are presigned URLs (expire after 5 minutes)
- If R2 is disabled, `cover_image_url` and `gallery_image_urls` will be `null`

---

### 8. Get Active Program (Public)
**GET** `/api/programs/active`

**Permission Required:** None (Public endpoint)

**Description:** Returns the currently active meditation program without needing to know its ID. Since only one program can be active at a time, this endpoint provides easy access to the current program.

**Response:** `200 OK`
```json
{
  "meditation_program_id": 1,
  "name": "Vipassana Meditation Retreat",
  "description": "10-day intensive Vipassana meditation retreat focused on developing mindfulness and insight...",
  "max_seats": 50,
  "cover_image_url": "https://r2.meditation-center.com/programs/1/cover/uuid.jpg?presigned=...",
  "gallery_image_urls": [
    "https://r2.meditation-center.com/programs/1/gallery/uuid1.jpg?presigned=...",
    "https://r2.meditation-center.com/programs/1/gallery/uuid2.jpg?presigned=..."
  ]
}
```

**Error Responses:**
- `404 NOT FOUND` - No active program exists
```json
{
  "timestamp": "2025-12-13T08:45:50.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "No active meditation program found",
  "path": "/api/programs/active"
}
```

**Important Notes:**
- Only returns the program where `is_active = true`
- Image URLs are presigned URLs (expire after 5 minutes)
- If R2 is disabled, `cover_image_url` and `gallery_image_urls` will be `null`
- If multiple programs are active (rare edge case), returns the most recently created one
- Returns 404 if no program is currently active

---

## Schedule Endpoints (Public)

### 9. Get Today's Schedule
**GET** `/api/schedule/today`

**Permission Required:** None (Public endpoint)

**Note:** Returns today's schedule. Checks for override first, then falls back to active template. No authentication required.

**Response:** `200 OK` (Override exists)
```json
{
  "schedule_date": "2025-12-25",
  "schedule_type": "OVERRIDE",
  "schedule_name": "Special Schedule",
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "activity_description": "Guided morning meditation session",
      "start_time": "07:00",
      "end_time": "08:00",
      "notes": "Special Christmas morning meditation"
    },
    {
      "activity_id": 3,
      "activity_title": "Dharma Talk",
      "activity_description": "Daily wisdom teachings",
      "start_time": "10:00",
      "end_time": "11:30",
      "notes": "Holiday dharma talk"
    }
  ]
}
```

**Response:** `200 OK` (Template schedule)
```json
{
  "schedule_date": "2025-12-05",
  "schedule_type": "TEMPLATE",
  "schedule_name": "Weekday Schedule",
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "activity_description": "Guided morning meditation session",
      "start_time": "05:00",
      "end_time": "06:00",
      "notes": "Morning meditation session"
    },
    {
      "activity_id": 2,
      "activity_title": "Breakfast",
      "activity_description": "Community breakfast time",
      "start_time": "06:00",
      "end_time": "07:00",
      "notes": "Breakfast and community time"
    }
  ]
}
```

**Response:** `200 OK` (No schedule)
```json
{
  "schedule_date": "2025-12-05",
  "schedule_type": "NONE",
  "schedule_name": "No Schedule",
  "activities": []
}
```

---

### 10. Get Schedule by Date
**GET** `/api/schedule/{date}`

**Permission Required:** None (Public endpoint)

**Note:** Returns schedule for the specified date. Date format in URL should be `yyyy-MM-dd`. Checks for override first, then falls back to active template. No authentication required.

**Example:** `GET /api/schedule/2025-12-25`

**Response:** Same format as "Get Today's Schedule" above

---

## Book Endpoints (Public)

### 11. Get Books
**GET** `/api/books?limit=20&offset=0`

**Permission Required:** None (Public endpoint)

**Query Parameters:**
- `limit` (optional, default: 20, max: 100) - Number of results per page
- `offset` (optional, default: 0) - Page offset for pagination

**Response:** `200 OK`
```json
{
  "data": [
    {
      "book_id": 1,
      "title": "The Art of Meditation",
      "author": "Venerable Narada Thera",
      "description": "A comprehensive guide to Buddhist meditation practices and techniques for beginners and advanced practitioners.",
      "pdf_url": "https://r2.example.com/meditation-center-books/books/1/pdf/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf?X-Amz-Algorithm=...",
      "cover_image_url": "https://r2.example.com/meditation-center-books/books/1/cover/b2c3d4e5-f6a7-8901-bcde-f12345678901.jpg?X-Amz-Algorithm=..."
    },
    {
      "book_id": 2,
      "title": "Mindfulness in Plain English",
      "author": "Bhante Henepola Gunaratana",
      "description": "A practical guide to mindfulness meditation.",
      "pdf_url": "https://r2.example.com/meditation-center-books/books/2/pdf/c3d4e5f6-a7b8-9012-cdef-123456789012.pdf?X-Amz-Algorithm=...",
      "cover_image_url": null
    }
  ],
  "currentOffset": 0,
  "maxOffset": 25
}
```

**Important Notes:**
- Returns only **active** books (`is_active = true`)
- Books ordered by `created_at` descending (newest first)
- `pdf_url` and `cover_image_url` are presigned URLs with **15-minute expiry**
- URLs must be used immediately or refreshed by re-fetching
- `cover_image_url` is `null` if no cover image uploaded
- Pagination: `offset` is page-based, multiply by `limit` for row offset
- No authentication required

---

## Admin Event Management

### 12. Create Event (Multipart)
**POST** `/api/admin/event`

**Permission Required:** `ADMIN` role + `CREATE_EVENT` permission

**Content-Type:** `multipart/form-data`

**Request Parts:**
- `event` (required): JSON string of event data
- `coverImage` (optional): Cover image file (JPEG, PNG, GIF, WebP, max 5MB)
- `galleryImages` (optional): Multiple gallery image files (JPEG, PNG, GIF, WebP, max 5MB each)

**Event JSON:**
```json
{
  "name": "Full Moon Meditation",
  "description": "Special full moon meditation ceremony",
  "eventDate": "2025-12-15",
  "startTime": "18:00",
  "endTime": "20:00",
  "location": "Main Hall",
  "isActive": true
}
```

**Response:** `201 CREATED`
```json
{
  "event_id": 1,
  "name": "Full Moon Meditation",
  "description": "Special full moon meditation ceremony",
  "event_date": "2025-12-15",
  "start_time": "18:00",
  "end_time": "20:00",
  "location": "Main Hall",
  "cover_image_key": "events/1/cover-20251210.jpg",
  "gallery_image_keys": [
    "events/1/gallery-1-20251210.jpg",
    "events/1/gallery-2-20251210.jpg"
  ],
  "is_active": true,
  "created_at": "2025-12-10T10:30:00",
  "updated_at": "2025-12-10T10:30:00"
}
```

---

### 12. Create Event (JSON Only)
**POST** `/api/admin/event/json`

**Permission Required:** `ADMIN` role + `CREATE_EVENT` permission

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "Full Moon Meditation",
  "description": "Special full moon meditation ceremony",
  "eventDate": "2025-12-15",
  "startTime": "18:00",
  "endTime": "20:00",
  "location": "Main Hall",
  "isActive": true
}
```

**Response:** `201 CREATED` (same format as multipart endpoint)

**Note:** Use this endpoint for creating events without images, or for backward compatibility.

---

### 13. Get Admin Events
**GET** `/api/admin/event?limit=20&offset=0`

**Permission Required:** `ADMIN` role

**Query Parameters:**
- `limit` (optional, default: 20, max: 100) - Number of results per page
- `offset` (optional, default: 0) - Pagination offset

**Response:** `200 OK`
```json
{
  "data": [
    {
      "event_id": 1,
      "name": "Full Moon Meditation",
      "description": "Special full moon meditation ceremony",
      "event_date": "2025-12-15",
      "start_time": "18:00",
      "end_time": "20:00",
      "location": "Main Hall",
      "cover_image_url": "https://presigned-url.cloudflare.com/...",
      "gallery_image_urls": [
        "https://presigned-url.cloudflare.com/...",
        "https://presigned-url.cloudflare.com/..."
      ]
    }
  ],
  "currentOffset": 0,
  "maxOffset": 5
}
```

---

### 14. Update Event
**PATCH** `/api/admin/event/{eventId}`

**Permission Required:** `ADMIN` role + `UPDATE_EVENT` permission

**Note:** This is a partial update - only provided fields will be updated. All fields are optional.

**Request Body:** (All fields optional)
```json
{
  "name": "Updated Full Moon Meditation",
  "description": "Updated special full moon meditation ceremony",
  "eventDate": "2025-12-20",
  "startTime": "19:00",
  "endTime": "21:00",
  "location": "Meditation Hall B",
  "isActive": false
}
```

**Response:** `200 OK`
```json
{
  "event_id": 1,
  "name": "Updated Full Moon Meditation",
  "description": "Updated special full moon meditation ceremony",
  "event_date": "2025-12-20",
  "start_time": "19:00",
  "end_time": "21:00",
  "location": "Meditation Hall B",
  "cover_image_key": "events/1/cover-20251210.jpg",
  "gallery_image_keys": [
    "events/1/gallery-1-20251210.jpg",
    "events/1/gallery-2-20251210.jpg"
  ],
  "is_active": false,
  "updated_at": "2025-12-10T15:30:00"
}
```

**Response:** `404 NOT FOUND` (if event doesn't exist)
```json
{
  "status": 404,
  "message": "Event not found with ID: 999"
}
```

**Important Notes:**
- Only provided fields will be updated
- Images cannot be updated through this endpoint
- The `updated_at` timestamp is automatically updated
- Event validation rules still apply (e.g., eventDate must be in the future if provided)

---

### 15. Delete Event
**DELETE** `/api/admin/event/{eventId}`

**Permission Required:** `ADMIN` role + `DELETE_EVENT` permission

**Note:** This is a hard delete - permanently removes the event and all associated images from R2 storage.

**Response:** `200 OK`
```json
{
  "event_id": 1,
  "message": "Event 'Full Moon Meditation' (ID: 1) has been successfully deleted",
  "images_deleted": true
}
```

**Response:** `404 NOT FOUND` (if event doesn't exist)
```json
{
  "event_id": 999,
  "message": "Event with ID 999 not found",
  "images_deleted": false
}
```

---

## Admin Program Management

### 16. Create Program (Multipart)
**POST** `/api/admin/program`

**Permission Required:** `ADMIN` role + `CREATE_PROGRAM` permission

**Content-Type:** `multipart/form-data`

**Request Parts:**
- `program` (required) - JSON string of program data
- `coverImage` (optional) - Cover image file (JPEG, PNG, GIF, WebP, max 5MB)
- `galleryImages` (optional) - Multiple gallery image files (max 5MB each)

**Program JSON:**
```json
{
  "name": "Vipassana Meditation Retreat",
  "description": "10-day intensive Vipassana meditation retreat...",
  "maxSeats": 50,
  "isActive": true
}
```

**Response:** `201 CREATED`
```json
{
  "meditation_program_id": 1,
  "name": "Vipassana Meditation Retreat",
  "description": "10-day intensive Vipassana meditation retreat...",
  "max_seats": 50,
  "cover_image_key": "programs/1/cover/uuid.jpg",
  "gallery_image_keys": [
    "programs/1/gallery/uuid1.jpg",
    "programs/1/gallery/uuid2.jpg"
  ],
  "is_active": true,
  "created_at": "2025-12-13T08:45:50.123",
  "updated_at": "2025-12-13T08:45:50.123"
}
```

**Error Responses:**
- `400 BAD REQUEST` - Validation error or invalid image
```json
{
  "timestamp": "2025-12-13T08:45:50.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid cover image: File size exceeds 5MB limit",
  "path": "/api/admin/program"
}
```
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing CREATE_PROGRAM permission

**Important Notes:**
- Returns image KEYS (not URLs)
- If R2 is disabled, images will not be uploaded and keys will be `null`
- All fields except `name` are optional
- `isActive` defaults to `true` if not provided
- `maxSeats` defaults to `0` if not provided

---

### 17. Create Program (JSON Only)
**POST** `/api/admin/program/json`

**Permission Required:** `ADMIN` role + `CREATE_PROGRAM` permission

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "name": "Mindfulness Meditation Program",
  "description": "8-week program for beginners...",
  "maxSeats": 30,
  "isActive": true
}
```

**Response:** `201 CREATED` (same as multipart endpoint)

**Important Notes:**
- Backward compatibility endpoint for creating programs without images
- Images can be uploaded later via separate endpoints (if implemented)

---

### 18. Get Active Program (Admin)
**GET** `/api/admin/programs/active`

**Permission Required:** `ADMIN` role + `VIEW_PROGRAM` permission

**Description:** Returns the currently active meditation program for admin management. Since only one program can be active at a time, this endpoint provides easy access without needing the ID.

**Response:** `200 OK`
```json
{
  "meditation_program_id": 1,
  "name": "Vipassana Meditation Retreat",
  "description": "10-day intensive Vipassana meditation retreat focused on developing mindfulness and insight...",
  "max_seats": 50,
  "cover_image_url": "https://r2.meditation-center.com/programs/1/cover/uuid.jpg?presigned=...",
  "gallery_image_urls": [
    "https://r2.meditation-center.com/programs/1/gallery/uuid1.jpg?presigned=...",
    "https://r2.meditation-center.com/programs/1/gallery/uuid2.jpg?presigned=..."
  ],
  "is_active": true,
  "created_at": "2025-12-13T08:45:50.123",
  "updated_at": "2025-12-13T08:45:50.123"
}
```

**Error Responses:**
- `404 NOT FOUND` - No active program exists
```json
{
  "timestamp": "2025-12-13T08:45:50.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "No active meditation program found",
  "path": "/api/admin/programs/active"
}
```
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing VIEW_PROGRAM permission

**Important Notes:**
- Returns full admin details including `is_active`, `created_at`, and `updated_at`
- Image URLs are presigned URLs (expire after 5 minutes)
- If R2 is disabled, image URLs will be `null`
- If multiple programs are active (rare edge case), returns the most recently created one
- Returns 404 if no program is currently active

---

### 19. Get Program by ID (Admin)
**GET** `/api/admin/programs/{programId}`

**Permission Required:** `ADMIN` role + `VIEW_PROGRAM` permission

**Path Parameters:**
- `programId` (Long) - Program ID

**Response:** `200 OK`
```json
{
  "meditation_program_id": 1,
  "name": "Vipassana Meditation Retreat",
  "description": "10-day intensive Vipassana meditation retreat...",
  "max_seats": 50,
  "cover_image_url": "https://r2.meditation-center.com/programs/1/cover/uuid.jpg?presigned=...",
  "gallery_image_urls": [
    "https://r2.meditation-center.com/programs/1/gallery/uuid1.jpg?presigned=...",
    "https://r2.meditation-center.com/programs/1/gallery/uuid2.jpg?presigned=..."
  ],
  "is_active": true,
  "created_at": "2025-12-13T08:45:50.123",
  "updated_at": "2025-12-13T08:45:50.123"
}
```

**Error Responses:**
- `404 NOT FOUND` - Program not found
```json
{
  "timestamp": "2025-12-13T08:45:50.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Meditation program not found with ID: 999",
  "path": "/api/admin/programs/999"
}
```
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing VIEW_PROGRAM permission

**Important Notes:**
- Returns programs regardless of `is_active` status (unlike public endpoint)
- Returns presigned image URLs (not keys)
- Image URLs expire after 5 minutes
- If R2 is disabled, image URLs will be `null`

---

### 20. Update Program
**PATCH** `/api/admin/programs/{programId}`

**Permission Required:** `ADMIN` role + `UPDATE_PROGRAM` permission

**Path Parameters:**
- `programId` (Long) - Program ID

**Content-Type:** `application/json`

**Request Body:** (All fields optional)
```json
{
  "name": "Updated Program Name",
  "description": "Updated description...",
  "maxSeats": 60,
  "isActive": false
}
```

**Response:** `200 OK`
```json
{
  "meditation_program_id": 1,
  "name": "Updated Program Name",
  "description": "Updated description...",
  "max_seats": 60,
  "cover_image_url": "https://r2.meditation-center.com/programs/1/cover/uuid.jpg?presigned=...",
  "gallery_image_urls": [
    "https://r2.meditation-center.com/programs/1/gallery/uuid1.jpg?presigned=..."
  ],
  "is_active": false,
  "updated_at": "2025-12-13T09:00:00.123"
}
```

**Error Responses:**
- `400 BAD REQUEST` - Validation error
```json
{
  "timestamp": "2025-12-13T08:45:50.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Program name must be between 3 and 255 characters",
  "path": "/api/admin/programs/1"
}
```
- `404 NOT FOUND` - Program not found
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing UPDATE_PROGRAM permission

**Important Notes:**
- Partial update - only provided fields are updated
- Cannot update images via this endpoint (use multipart endpoints if implemented)
- `updated_at` timestamp is automatically set
- Returns presigned image URLs
- **BUSINESS LOGIC:** When setting `isActive: true`, all other programs are automatically deactivated to ensure only one program is active at a time

---

## Admin Book Management

### 21. Create Book
**POST** `/api/admin/book`

**Permission Required:** `ADMIN` role + `CREATE_BOOK` permission

**Content-Type:** `multipart/form-data`

**Request Parts:**
- `book` (required) - JSON string containing book metadata
- `pdfFile` (required) - PDF file (max 50MB)
- `coverImage` (optional) - Cover image file (JPEG, PNG, GIF, WebP, max 5MB)

**Book JSON Structure:**
```json
{
  "title": "The Art of Meditation",
  "author": "Venerable Narada Thera",
  "description": "A comprehensive guide to Buddhist meditation practices and techniques for beginners and advanced practitioners."
}
```

**Example using cURL:**
```bash
curl -X POST http://localhost:8080/api/admin/book \
  -H "Authorization: Bearer eyJhbGci..." \
  -F 'book={"title":"The Art of Meditation","author":"Venerable Narada Thera","description":"A comprehensive guide to Buddhist meditation practices"}' \
  -F 'pdfFile=@/path/to/meditation-guide.pdf' \
  -F 'coverImage=@/path/to/cover.jpg'
```

**Response:** `201 CREATED`
```json
{
  "book_id": 1,
  "title": "The Art of Meditation",
  "author": "Venerable Narada Thera",
  "description": "A comprehensive guide to Buddhist meditation practices and techniques for beginners and advanced practitioners.",
  "pdf_file_key": "books/1/pdf/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf",
  "cover_image_key": "books/1/cover/b2c3d4e5-f6a7-8901-bcde-f12345678901.jpg",
  "pdf_url": "https://r2.example.com/meditation-center-books/books/1/pdf/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf?X-Amz-Algorithm=...",
  "cover_image_url": "https://r2.example.com/meditation-center-books/books/1/cover/b2c3d4e5-f6a7-8901-bcde-f12345678901.jpg?X-Amz-Algorithm=...",
  "is_active": true,
  "created_at": "2025-12-14T10:30:00",
  "updated_at": "2025-12-14T10:30:00"
}
```

**Error Responses:**

**400 BAD REQUEST** - Validation errors
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "PDF file is required",
  "path": "/api/admin/book"
}
```

**400 BAD REQUEST** - Invalid PDF file
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid PDF file: File is not a valid PDF (magic byte verification failed)",
  "path": "/api/admin/book"
}
```

**400 BAD REQUEST** - File size exceeded
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid PDF file: File size exceeds maximum allowed size of 50MB",
  "path": "/api/admin/book"
}
```

**400 BAD REQUEST** - Invalid cover image
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid cover image: File is not a valid image (magic byte verification failed)",
  "path": "/api/admin/book"
}
```

**401 UNAUTHORIZED** - Not authenticated
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/admin/book"
}
```

**403 FORBIDDEN** - Missing CREATE_BOOK permission
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/admin/book"
}
```

**500 INTERNAL SERVER ERROR** - File upload failed
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Failed to upload book files: Connection timeout",
  "path": "/api/admin/book"
}
```

**Important Notes:**
- **PDF Validation:** Files are validated using magic byte verification (%PDF- header)
- **File Size Limits:** PDF max 50MB, cover image max 5MB
- **Presigned URLs:** Generated with 15-minute expiry for downloads
- **File Organization:** PDFs stored at `books/{bookId}/pdf/{uuid}.pdf`, covers at `books/{bookId}/cover/{uuid}.{ext}`
- **R2 Storage:** If R2 is disabled, book will be created without file keys (for testing only)
- **Transactional:** Book creation and file upload are wrapped in a transaction
- **Security:** All files validated before upload to prevent malicious content
- **Books are free:** All authenticated users can view and download books (VIEW_BOOKS, DOWNLOAD_BOOK permissions)

---

### 22. Create Book (JSON-only)
**POST** `/api/admin/book/json`

**Permission Required:** `ADMIN` role + `CREATE_BOOK` permission

**Content-Type:** `application/json`

**Note:** This endpoint is primarily for testing when R2 is disabled. In production, use the multipart endpoint above.

**Request Body:**
```json
{
  "title": "The Art of Meditation",
  "author": "Venerable Narada Thera",
  "description": "A comprehensive guide to Buddhist meditation practices and techniques for beginners and advanced practitioners."
}
```

**Response:** `201 CREATED`
```json
{
  "book_id": 1,
  "title": "The Art of Meditation",
  "author": "Venerable Narada Thera",
  "description": "A comprehensive guide to Buddhist meditation practices and techniques for beginners and advanced practitioners.",
  "pdf_file_key": null,
  "cover_image_key": null,
  "pdf_url": null,
  "cover_image_url": null,
  "is_active": true,
  "created_at": "2025-12-14T10:30:00",
  "updated_at": "2025-12-14T10:30:00"
}
```

**Important Notes:**
- File keys and URLs will be `null` when created without files
- Primarily used for testing purposes
- Production should use multipart endpoint with actual PDF files

---

### 23. Get All Books (Admin)
**GET** `/api/admin/book?limit=20&offset=0`

**Permission Required:** `ADMIN` role + `VIEW_BOOKS` permission

**Query Parameters:**
- `limit` (optional, default: 20, max: 100) - Number of results per page
- `offset` (optional, default: 0) - Page offset for pagination

**Response:** `200 OK`
```json
{
  "data": [
    {
      "book_id": 1,
      "title": "The Art of Meditation",
      "author": "Venerable Narada Thera",
      "description": "A comprehensive guide to Buddhist meditation practices and techniques for beginners and advanced practitioners.",
      "pdf_url": "https://r2.example.com/meditation-center-books/books/1/pdf/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf?X-Amz-Algorithm=...",
      "cover_image_url": "https://r2.example.com/meditation-center-books/books/1/cover/b2c3d4e5-f6a7-8901-bcde-f12345678901.jpg?X-Amz-Algorithm=...",
      "is_active": true
    },
    {
      "book_id": 2,
      "title": "Mindfulness in Plain English",
      "author": "Bhante Henepola Gunaratana",
      "description": "A practical guide to mindfulness meditation.",
      "pdf_url": "https://r2.example.com/meditation-center-books/books/2/pdf/c3d4e5f6-a7b8-9012-cdef-123456789012.pdf?X-Amz-Algorithm=...",
      "cover_image_url": null,
      "is_active": true
    },
    {
      "book_id": 3,
      "title": "Archived Book",
      "author": "Test Author",
      "description": "This book has been deactivated.",
      "pdf_url": "https://r2.example.com/meditation-center-books/books/3/pdf/d4e5f6a7-b8c9-0123-def4-567890123456.pdf?X-Amz-Algorithm=...",
      "cover_image_url": null,
      "is_active": false
    }
  ],
  "currentOffset": 0,
  "maxOffset": 35
}
```

**Important Notes:**
- Returns **all** books (active and inactive) - unlike public endpoint
- Includes `is_active` field to distinguish active/inactive books
- Books ordered by `created_at` descending (newest first)
- `pdf_url` and `cover_image_url` are presigned URLs with **15-minute expiry**
- URLs must be used immediately or refreshed by re-fetching
- `cover_image_url` is `null` if no cover image uploaded
- Pagination: `offset` is page-based, multiply by `limit` for row offset
- Requires ADMIN role and VIEW_BOOKS permission

---

### 24. Update Book
**PATCH** `/api/admin/book/{bookId}`

**Permission Required:** `ADMIN` role + `UPDATE_BOOK` permission

**Content-Type:** `application/json`

**Path Parameters:**
- `bookId` (required) - The ID of the book to update

**Request Body (all fields optional):**
```json
{
  "title": "Updated Title",
  "author": "Updated Author Name",
  "description": "Updated description text.",
  "is_active": false
}
```

**Example - Update only title:**
```json
{
  "title": "The New Art of Meditation"
}
```

**Example - Toggle active status:**
```json
{
  "is_active": false
}
```

**Example - Update multiple fields:**
```json
{
  "title": "Mindfulness for Beginners",
  "description": "An updated comprehensive guide for those starting their mindfulness journey.",
  "is_active": true
}
```

**Response:** `200 OK`
```json
{
  "book_id": 1,
  "title": "The New Art of Meditation",
  "author": "Venerable Narada Thera",
  "description": "A comprehensive guide to Buddhist meditation practices and techniques for beginners and advanced practitioners.",
  "pdf_url": "https://r2.example.com/meditation-center-books/books/1/pdf/a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf?X-Amz-Algorithm=...",
  "cover_image_url": "https://r2.example.com/meditation-center-books/books/1/cover/b2c3d4e5-f6a7-8901-bcde-f12345678901.jpg?X-Amz-Algorithm=...",
  "is_active": true,
  "message": "Book updated successfully"
}
```

**Error Responses:**

**400 BAD REQUEST** - No fields provided
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "At least one field must be provided for update",
  "path": "/api/admin/book/1"
}
```

**400 BAD REQUEST** - Validation error
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Title must be between 1 and 255 characters",
  "path": "/api/admin/book/1"
}
```

**401 UNAUTHORIZED** - Not authenticated
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/admin/book/1"
}
```

**403 FORBIDDEN** - Missing UPDATE_BOOK permission
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/admin/book/1"
}
```

**404 NOT FOUND** - Book doesn't exist
```json
{
  "timestamp": "2025-12-14T10:30:00.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found with bookId: 999",
  "path": "/api/admin/book/999"
}
```

**Important Notes:**
- **Partial Update:** Only fields provided in the request will be updated
- **At least one field required:** You must provide at least one field to update
- **Validation:** Each field is validated according to its constraints (e.g., title max 255 characters)
- **Files cannot be updated:** This endpoint only updates metadata (title, author, description, is_active)
- **To update PDF or cover image:** Delete the book and create a new one (or use a future dedicated file update endpoint)
- **is_active toggle:** Use this to activate/deactivate books for public visibility
- **Presigned URLs:** Generated fresh with 15-minute expiry for the updated book
- **Transactional:** Update is wrapped in a transaction for data consistency
- **Common use cases:**
  - Deactivate book: `{"is_active": false}` - Hide from public
  - Reactivate book: `{"is_active": true}` - Make visible to public
  - Fix typos: `{"title": "Corrected Title"}`
  - Update descriptions: `{"description": "New detailed description"}`

---

## Admin User Management

### 26. Get All Users
**GET** `/api/admin/users?limit=20&offset=0&role=USER&isActive=true&search=john`

**Permission Required:** `ADMIN` role + `VIEW_USERS` permission

**Query Parameters:**
- `limit` (optional, default: 20, max: 100) - Number of results per page
- `offset` (optional, default: 0) - Pagination offset
- `role` (optional: USER, ADMIN) - Filter by user role
- `isActive` (optional: true/false) - Filter by active status
- `search` (optional) - Search by name or email (case-insensitive)

**Response:** `200 OK`
```json
{
  "data": [
    {
      "user_id": 1,
      "email": "user@example.com",
      "name": "John Doe",
      "mobile_number": "+94771234567",
      "role": "USER",
      "is_active": true,
      "email_verified": true,
      "avatar_url": null,
      "created_at": "2025-01-01T10:00:00",
      "updated_at": "2025-01-15T14:30:00"
    },
    {
      "user_id": 2,
      "email": "admin@meditationcenter.com",
      "name": "System Administrator",
      "mobile_number": "+94771234568",
      "role": "ADMIN",
      "is_active": true,
      "email_verified": true,
      "avatar_url": null,
      "created_at": "2024-12-01T09:00:00",
      "updated_at": "2025-01-10T11:20:00"
    }
  ],
  "currentOffset": 0,
  "maxOffset": 80
}
```

**Error Responses:**
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing VIEW_USERS permission

**Important Notes:**
- Results are ordered by `created_at` descending (newest first)
- Search is case-insensitive and searches both name and email fields
- Avatar URL will be `null` until avatar management is implemented
- Pagination follows standard offset-based pattern

---

### 22. Get User Details
**GET** `/api/admin/users/{userId}`

**Permission Required:** `ADMIN` role + `VIEW_USERS` permission

**Path Parameters:**
- `userId` (Long) - User ID

**Response:** `200 OK`
```json
{
  "user_id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "mobile_number": "+94771234567",
  "role": "USER",
  "is_active": true,
  "email_verified": true,
  "avatar_url": null,
  "created_at": "2025-01-01T10:00:00",
  "updated_at": "2025-01-15T14:30:00",
  "statistics": {
    "totalBookings": 5,
    "activeBookings": 2,
    "totalDonations": 15000.00,
    "eventRegistrations": 3
  }
}
```

**Error Responses:**
- `404 NOT FOUND` - User not found
```json
{
  "timestamp": "2025-12-13T11:00:00.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with ID: 999",
  "path": "/api/admin/users/999"
}
```
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing VIEW_USERS permission

**Important Notes:**
- Includes user statistics aggregated from multiple tables:
  - `totalBookings`: Count from `booking` table
  - `activeBookings`: Count of bookings with status CONFIRMED or PENDING
  - `totalDonations`: Sum of amounts from `donation` table
  - `eventRegistrations`: Count from `event_registration` table (currently 0 - table not yet implemented)
- Statistics are calculated in real-time
- All statistics default to 0/zero if no data exists

---

### 23. Create User
**POST** `/api/admin/users`

**Permission Required:** `ADMIN` role + `CREATE_USER` permission

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "SecurePass123!",
  "name": "Jane Smith",
  "mobile_number": "+94771234569",
  "role": "USER",
  "is_active": true,
  "email_verified": false
}
```

**Field Validations:**
- `email` (required) - Must be a valid email format
- `password` (required) - Minimum 8 characters
- `name` (required) - User's full name
- `mobile_number` (optional) - Contact number
- `role` (required) - USER or ADMIN
- `is_active` (optional, default: true) - Account status
- `email_verified` (optional, default: false) - Email verification status

**Response:** `201 CREATED`
```json
{
  "user_id": 3,
  "email": "newuser@example.com",
  "name": "Jane Smith",
  "mobile_number": "+94771234569",
  "role": "USER",
  "is_active": true,
  "email_verified": false,
  "created_at": "2025-12-14T10:00:00",
  "updated_at": "2025-12-14T10:00:00"
}
```

**Error Responses:**
- `400 BAD REQUEST` - Validation errors
```json
{
  "timestamp": "2025-12-14T10:00:00.123+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Email must be valid",
    "password": "Password must be at least 8 characters"
  }
}
```
- `409 CONFLICT` - Email already exists
```json
{
  "timestamp": "2025-12-14T10:00:00.123+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "User with email newuser@example.com already exists",
  "path": "/api/admin/users"
}
```
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing CREATE_USER permission

**Important Notes:**
- Password is automatically hashed before storage (using BCrypt)
- Email must be unique across all users
- Default values apply if `is_active` or `email_verified` are not provided
- Created user does NOT receive a welcome email (must be implemented separately)
- User ID is auto-generated

---

### 24. Update User
**PATCH** `/api/admin/users/{userId}`

**Permission Required:** `ADMIN` role + `UPDATE_USER` permission

**Path Parameters:**
- `userId` (Long) - User ID to update

**Request Body:** (All fields optional - only provided fields will be updated)
```json
{
  "email": "updatedemail@example.com",
  "password": "NewSecurePass456!",
  "name": "Jane Smith Updated",
  "mobile_number": "+94771234570"
}
```

**Field Validations:**
- `email` (optional) - Must be a valid email format if provided
- `password` (optional) - Minimum 8 characters if provided
- `name` (optional) - User's full name
- `mobile_number` (optional) - Contact number

**Response:** `200 OK`
```json
{
  "user_id": 3,
  "email": "updatedemail@example.com",
  "name": "Jane Smith Updated",
  "mobile_number": "+94771234570",
  "role": "USER",
  "is_active": true,
  "email_verified": false,
  "created_at": "2025-12-14T10:00:00",
  "updated_at": "2025-12-14T11:30:00"
}
```

**Error Responses:**
- `404 NOT FOUND` - User not found
```json
{
  "timestamp": "2025-12-14T11:30:00.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with ID: 999",
  "path": "/api/admin/users/999"
}
```
- `409 CONFLICT` - Email already exists for another user
```json
{
  "timestamp": "2025-12-14T11:30:00.123+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email updatedemail@example.com already exists",
  "path": "/api/admin/users/3"
}
```
- `400 BAD REQUEST` - Validation errors
- `401 UNAUTHORIZED` - Not authenticated
- `403 FORBIDDEN` - Missing UPDATE_USER permission

**Important Notes:**
- This is a PATCH endpoint - only fields included in the request will be updated
- Password is automatically hashed if provided
- Email uniqueness is validated (cannot change to an email already in use)
- Role and account status (is_active, email_verified) cannot be changed via this endpoint
  - Use dedicated endpoints for role management and activation/deactivation
- Updated timestamp is automatically set

---

## Admin Activity Management

### 26. Create Activity
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

### 22. Get All Activities
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

### 23. Get Single Activity
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

### 24. Update Activity
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

### 26. Delete Activity
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

## Admin Template Management

### 27. Create Template
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

### 28. Get All Templates
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

### 29. Get Active Template
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

### 30. Get Template by ID
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

### 31. Update Template (Full Replacement)
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

### 32. Activate Template
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

### 33. Delete Template
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

### 34. Add Activity to Template
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

### 35. Update Template Activity
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

### 36. Remove Activity from Template
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

### 37. Bulk Update Template Activities
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

## Admin Override Management

### 38. Create Override
**POST** `/api/admin/overrides`

**Permission Required:** `ADMIN` role + `CREATE_TEMPLATE` permission

**Note:** Creates a schedule override for a specific date. Only one override per date is allowed.

**Request Body:**
```json
{
  "overrideDate": "2025-12-25",
  "activities": [
    {
      "activityId": 1,
      "startTime": "07:00",
      "endTime": "08:00",
      "notes": "Special Christmas morning meditation"
    },
    {
      "activityId": 3,
      "startTime": "10:00",
      "endTime": "11:30",
      "notes": "Holiday dharma talk"
    }
  ]
}
```

**Response:** `201 CREATED`
```json
{
  "override_id": 1,
  "override_date": "2025-12-25",
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "start_time": "07:00",
      "end_time": "08:00",
      "notes": "Special Christmas morning meditation"
    },
    {
      "activity_id": 3,
      "activity_title": "Dharma Talk",
      "start_time": "10:00",
      "end_time": "11:30",
      "notes": "Holiday dharma talk"
    }
  ],
  "created_at": "2025-12-04T10:30:00"
}
```

---

### 39. Get All Overrides
**GET** `/api/admin/overrides?page=1&limit=10&fromDate=2025-12-01&toDate=2025-12-31`

**Permission Required:** `ADMIN` role + `VIEW_TEMPLATES` permission

**Query Parameters:**
- `page` (optional, default: 1) - Page number
- `limit` (optional, default: 10) - Items per page
- `fromDate` (optional, format: yyyy-MM-dd) - Filter overrides from this date
- `toDate` (optional, format: yyyy-MM-dd) - Filter overrides until this date

**Response:** `200 OK`
```json
{
  "data": [
    {
      "override_id": 1,
      "override_date": "2025-12-25",
      "activity_count": 2,
      "created_at": "2025-12-04T10:30:00",
      "updated_at": "2025-12-04T10:30:00"
    },
    {
      "override_id": 2,
      "override_date": "2025-12-31",
      "activity_count": 3,
      "created_at": "2025-12-05T09:00:00",
      "updated_at": "2025-12-05T09:00:00"
    }
  ],
  "currentOffset": 0,
  "maxOffset": 1
}
```

---

### 40. Get Override by Date
**GET** `/api/admin/overrides/{date}`

**Permission Required:** `ADMIN` role + `VIEW_TEMPLATES` permission

**Note:** Date format in URL should be `yyyy-MM-dd` (e.g., `2025-12-25`)

**Response:** `200 OK`
```json
{
  "override_id": 1,
  "override_date": "2025-12-25",
  "activities": [
    {
      "override_activity_id": 1,
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "activity_description": "Guided morning meditation session",
      "start_time": "07:00",
      "end_time": "08:00",
      "notes": "Special Christmas morning meditation"
    },
    {
      "override_activity_id": 2,
      "activity_id": 3,
      "activity_title": "Dharma Talk",
      "activity_description": "Daily wisdom teachings",
      "start_time": "10:00",
      "end_time": "11:30",
      "notes": "Holiday dharma talk"
    }
  ],
  "created_at": "2025-12-04T10:30:00",
  "updated_at": "2025-12-04T10:30:00"
}
```

---

### 41. Update Override
**PUT** `/api/admin/overrides/{id}`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Note:** This replaces the override date and ALL activities. All existing activities will be deleted and replaced.

**Request Body:**
```json
{
  "overrideDate": "2025-12-25",
  "activities": [
    {
      "activityId": 1,
      "startTime": "06:30",
      "endTime": "07:30",
      "notes": "Updated morning meditation time"
    },
    {
      "activityId": 2,
      "startTime": "08:00",
      "endTime": "09:00",
      "notes": "Special breakfast"
    }
  ]
}
```

**Response:** `200 OK`
```json
{
  "override_id": 1,
  "override_date": "2025-12-25",
  "activities": [
    {
      "activity_id": 1,
      "activity_title": "Morning Meditation",
      "start_time": "06:30",
      "end_time": "07:30",
      "notes": "Updated morning meditation time"
    },
    {
      "activity_id": 2,
      "activity_title": "Breakfast",
      "start_time": "08:00",
      "end_time": "09:00",
      "notes": "Special breakfast"
    }
  ],
  "updated_at": "2025-12-04T11:00:00"
}
```

---

### 42. Delete Override
**DELETE** `/api/admin/overrides/{id}`

**Permission Required:** `ADMIN` role + `DELETE_TEMPLATE` permission

**Note:** Deleting an override automatically deletes all associated activities (CASCADE).

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Override for date '2025-12-25' (ID: 1) deleted successfully",
  "override_id": 1,
  "override_date": "2025-12-25"
}
```

---

### 43. Add Activity to Override
**POST** `/api/admin/overrides/{id}/activities`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Request Body:**
```json
{
  "activityId": 4,
  "startTime": "16:00",
  "endTime": "17:00",
  "notes": "Evening meditation for holiday"
}
```

**Response:** `201 CREATED`
```json
{
  "override_activity_id": 3,
  "override_id": 1,
  "activity_id": 4,
  "activity_title": "Evening Meditation",
  "start_time": "16:00",
  "end_time": "17:00",
  "notes": "Evening meditation for holiday",
  "created_at": "2025-12-04T11:30:00"
}
```

---

### 44. Remove Activity from Override
**DELETE** `/api/admin/overrides/{overrideId}/activities/{activityId}`

**Permission Required:** `ADMIN` role + `UPDATE_TEMPLATE` permission

**Note:** The `activityId` in the URL is the `override_activity_id`, NOT the activity ID.

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "Activity 'Evening Meditation' removed from override successfully",
  "override_activity_id": 3,
  "override_id": 1,
  "activity_title": "Evening Meditation"
}
```

---

## Utility Endpoints (Development Only)

### 45. Generate Password Hash
**GET** `/api/util/hash?password=admin123`

**Permission Required:** None (Public endpoint)

**Query Parameters:**
- `password` (required) - Plain text password to hash

**Response:** `200 OK`
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

**⚠️ WARNING:** This endpoint should be REMOVED or SECURED before production! It's only for development use to generate BCrypt password hashes for testing.

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

### 409 Conflict
```json
{
  "status": 409,
  "message": "Schedule override already exists for date: 2025-12-25"
}
```

---

## Notes

1. **Time Format:** All times use 24-hour format `HH:mm` (e.g., "05:00", "14:30")
2. **Date Format:** All dates use ISO 8601 format `yyyy-MM-dd` or `yyyy-MM-dd'T'HH:mm:ss`
3. **Authentication:** Include JWT token in header: `Authorization: Bearer <token>`
4. **Only One Active Template:** The system enforces that only one template can be active at a time
5. **CASCADE Deletion:** Deleting a template/override automatically deletes all its activities
6. **Activity ID vs Template/Override Activity ID:**
   - `activity_id` refers to the reusable activity definition
   - `template_activity_id` refers to a specific instance of an activity within a template
   - `override_activity_id` refers to a specific instance of an activity within an override
7. **Image URLs:** All image URLs in event responses are presigned URLs with 5-minute expiry
8. **Schedule Priority:** Override schedule takes precedence over template schedule for public endpoints

---

## Postman Collection Tips

1. Create environment variables:
   - `base_url`: `http://localhost:8080`
   - `token`: Your JWT Bearer token
   - `activity_id_1`: ID of first created activity
   - `template_id`: ID of created template
   - `override_id`: ID of created override
   - `event_id`: ID of created event

2. Set Authorization header for all admin requests:
   - Type: Bearer Token
   - Token: `{{token}}`

3. Save response IDs to environment variables for chaining requests

---

## Testing Workflow Example

### Step 1: Register and Login
```bash
# Register
POST /api/auth/register
{
  "email": "admin@example.com",
  "password": "SecurePass123",
  "name": "Admin User",
  "mobileNumber": "+94771234567"
}

# Login
POST /api/auth/login
{
  "email": "admin@example.com",
  "password": "SecurePass123"
}
# Save access_token for subsequent requests
```

### Step 2: Create Activities
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

### Step 3: Create Template with Activities
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

### Step 4: Activate Template
```bash
PATCH /api/admin/templates/1/activate
```

### Step 5: Create Override for Special Day
```bash
POST /api/admin/overrides
{
  "overrideDate": "2025-12-25",
  "activities": [
    {
      "activityId": 1,
      "startTime": "07:00",
      "endTime": "08:00",
      "notes": "Christmas special meditation"
    }
  ]
}
```

### Step 6: View Public Schedule
```bash
# Get today's schedule
GET /api/schedule/today

# Get specific date schedule
GET /api/schedule/2025-12-25
```

### Step 7: Create Event
```bash
# Using multipart/form-data with Postman
POST /api/admin/event
- event: {"name": "Full Moon Meditation", "eventDate": "2025-12-15", ...}
- coverImage: [file]
- galleryImages: [file1, file2]
```
