# Future Enhancements

This file tracks potential improvements and features to be implemented in the future.

---

## Media Handling for Activities

**Current Implementation:**
- Activities have a `media_url` field (VARCHAR 255)
- Supports external URLs (YouTube, SoundCloud, etc.)
- Simple approach - just stores the URL string

**Future Enhancement: Hybrid Media Approach**

### Problem
Currently, activities only support external URLs. We may want to:
1. Upload custom audio/video files (guided meditations, dharma talks)
2. Have control over our own media content
3. Still support external YouTube/Vimeo links

### Proposed Solution: Hybrid Approach

**Keep current schema** but add optional file upload capability:

```
Current:
- media_url VARCHAR(255) - stores ANY media URL (external or uploaded)

Examples:
- "https://www.youtube.com/watch?v=..."  (external YouTube)
- "https://r2.domain.com/activities/audio/morning-meditation.mp3" (uploaded to R2)
```

**Implementation Steps:**
1. Add new endpoint: `POST /api/admin/activities/{activityId}/upload-media`
   - Accepts multipart file upload (audio/video)
   - Uploads to Cloudflare R2 (reuse existing R2Service)
   - Updates `media_url` with R2 URL
   - Returns updated activity

2. Keep current endpoint for external URLs:
   - `POST /api/admin/activities` with `mediaUrl` in JSON body
   - Works for YouTube, SoundCloud, etc.

3. Optional: Add `GET /api/admin/activities/{activityId}/media/presigned-url`
   - For private uploaded media
   - Generates temporary presigned URL for playback

**Benefits:**
- Flexible - supports both uploaded and external media
- No schema changes needed
- Can use free YouTube content or upload custom recordings
- Full control over uploaded content

**Considerations:**
- Storage costs for uploaded files
- Need to handle file size limits (e.g., max 100MB)
- May want to track media type (audio/video) for UI rendering
- Could add `media_type` column later if needed

**Media Types to Support:**
- Audio: MP3, M4A, WAV (guided meditations, chanting)
- Video: MP4, WEBM (meditation instructions, dharma talks)
- External: YouTube, Vimeo, SoundCloud URLs

**Optional Future Schema Enhancement:**
```sql
ALTER TABLE activity ADD COLUMN media_type VARCHAR(20);
-- Values: 'youtube', 'vimeo', 'uploaded_audio', 'uploaded_video', 'external'

ALTER TABLE activity ADD COLUMN media_key VARCHAR(255);
-- R2 object key (only for uploaded files)
```

This would allow:
- Different UI rendering (YouTube embed vs audio player)
- Presigned URL generation for uploaded files only
- Better media management (delete uploaded files when activity is deleted)

---

## Admin User Management - Remaining Endpoints

**Status:** Deferred for future implementation

The following user management endpoints were planned but not yet implemented:

### 1. Update User Role
**PATCH** `/api/admin/users/{userId}/role`

**Purpose:** Change a user's role (USER ↔ ADMIN)

**Request Body:**
```json
{
  "role": "ADMIN"
}
```

**Features:**
- Updates only the `role` field
- Separate endpoint for security and audit purposes
- Should log role changes for compliance
- Requires: `ADMIN` role + `UPDATE_USER_ROLE` permission

**Implementation Notes:**
- Create dedicated endpoint for role changes
- Consider logging role changes to an audit table
- Validate role enum values
- Return updated user details

---

### 2. Activate User Account
**PATCH** `/api/admin/users/{userId}/activate`

**Purpose:** Reactivate a deactivated user account

**Request Body:** None (or empty JSON `{}`)

**Features:**
- Sets `is_active = true`
- Allows previously deactivated users to log in again
- Separate from general update for clarity and auditing
- Requires: `ADMIN` role + `MANAGE_USER_STATUS` permission

**Implementation Notes:**
- Simple update to set is_active flag
- Consider sending reactivation email notification
- Log activation event for audit trail

---

### 3. Deactivate User Account
**PATCH** `/api/admin/users/{userId}/deactivate`

**Purpose:** Deactivate a user account (soft delete)

**Request Body:**
```json
{
  "reason": "Policy violation - inappropriate behavior"
}
```

**Features:**
- Sets `is_active = false`
- Prevents user login without deleting data
- Soft delete alternative (preserves booking/donation history)
- Optional reason field for documentation
- Requires: `ADMIN` role + `MANAGE_USER_STATUS` permission

**Implementation Notes:**
- Consider adding `deactivation_reason` and `deactivated_at` columns
- Prevent deactivating yourself (current admin user)
- Log deactivation event with reason
- Consider email notification to user

---

### Additional User Management Features (Lower Priority)

**Not Planned for Immediate Implementation:**

- ❌ **DELETE /api/admin/users/{userId}** - Hard delete user
  - Decided to skip in favor of deactivation
  - Would need to handle cascading deletes for bookings/donations

- ❌ **POST /api/admin/users/{userId}/reset-password** - Admin-initiated password reset
  - Decided to skip for now
  - Would generate reset token and send email

- ❌ **PATCH /api/admin/users/{userId}/avatar** - Avatar upload/management
  - Noted as TODO in existing code (GetUsersPresenter.java:46)
  - Requires R2 integration for image storage
  - Should generate presigned URLs for avatar access

---

## Blog Admin Endpoints - Remaining Implementation

**Status:** Deferred for future implementation

The following blog admin endpoints were planned but not yet implemented:

### 1. Update Blog Post
**PATCH** `/api/admin/blog/{postId}`

**Purpose:** Update existing blog post with optional image replacement

**Request (Multipart):**
- `request` part (JSON): PatchBlogPostRequest with all optional fields
- `coverImage` part (optional): New cover image file
- `galleryImages` part (optional): New gallery images
- `deleteCoverImage` (boolean): Whether to delete existing cover image
- `deleteGalleryImages` (Set<String>): Keys of gallery images to delete

**Request Body Example:**
```json
{
  "title": "Updated Title",
  "excerpt": "Updated excerpt",
  "content": "Updated content...",
  "slug": "updated-slug",
  "tagIds": [1, 2, 3],
  "metaTitle": "Updated SEO title",
  "version": 5
}
```

**Features:**
- Dynamic UPDATE (only non-null fields updated)
- Optional image replacement:
  - Upload new cover image → deletes old, uploads new
  - Upload new gallery images → adds to existing or replaces all
  - Delete specific gallery images by key
- Tag association updates (replaces entire tag set)
- Slug uniqueness validation (if slug changed)
- Optimistic locking using version field
- Returns updated post with new version number

**Implementation Notes:**
- Use PatchBlogTagRepository pattern for dynamic UPDATE
- Handle image replacement carefully:
  - Delete old R2 objects before uploading new ones
  - Update image_keys array in database
- Version conflict should return 409 Conflict
- Validate slug uniqueness if slug is changed
- Requires: `UPDATE_BLOG_POST` permission

**Components Needed (9 files):**
- PatchBlogPostRequest.java
- PatchBlogPostResponse.java
- PatchBlogPostDataAccess.java
- PatchBlogPostRepository.java
- PatchBlogPostHttpDataAccess.java (delete + upload images)
- PatchBlogPostHttpRepository.java (R2FileManagerClient)
- PatchBlogPostResponseBuilder.java
- PatchBlogPostPresenter.java
- PatchBlogPostUseCase.java

---

### 2. Auto-Save Draft
**PATCH** `/api/admin/blog/drafts/{postId}`

**Purpose:** Periodic auto-save from editor (no validation, optimistic locking only)

**Request Body (JSON only - no multipart):**
```json
{
  "title": "Partially written title",
  "content": "Incomplete content...",
  "version": 3
}
```

**Features:**
- **NO validation** - allows saving incomplete drafts
- JSON only (no file uploads during auto-save)
- Only works on DRAFT status posts
- Optimistic locking using version field
- Returns 409 Conflict if version mismatch
- Returns updated post with new version number
- Fast, lightweight operation for frequent auto-saves

**Implementation Notes:**
- Skip all validation that PostBlogPost has
- Allow null/empty fields
- Only check: post exists, is DRAFT, version matches
- Simple UPDATE query with version check
- Return minimal response (just version + updated_at)
- Requires: `UPDATE_BLOG_POST` permission

**Components Needed (7 files):**
- AutoSaveBlogPostRequest.java
- AutoSaveBlogPostResponse.java (minimal - just version)
- AutoSaveBlogPostDataAccess.java
- AutoSaveBlogPostRepository.java (dynamic UPDATE + version check)
- AutoSaveBlogPostResponseBuilder.java
- AutoSaveBlogPostPresenter.java
- AutoSaveBlogPostUseCase.java

---

### Additional Blog Features (Lower Priority)

**Optional Enhancements Not Planned:**

- ❌ **Scheduled Publishing** - publishedAt in future = scheduled post
  - Would need background job to auto-publish at scheduled time
  - Can be added later with scheduler

- ❌ **Post Analytics** - Track views over time, popular tags
  - Would need analytics table (post_views with date/count)
  - Could generate charts of views over time

- ❌ **Related Posts** - Suggest related posts by tags
  - Algorithm to find posts with overlapping tags
  - Could be added to GET /api/blog/{slug} response

- ❌ **Draft Preview** - Preview draft posts before publishing
  - Generate temporary preview link for drafts
  - Would need separate public preview endpoint

- ❌ **Comment System** - User comments on blog posts
  - Would need new tables: comments, comment_likes
  - Moderation features for admins

- ❌ **RSS Feed Generation** - Auto-generate RSS feed
  - Endpoint: GET /api/blog/feed.xml
  - Standard RSS 2.0 format

- ❌ **Sitemap Generation** - Auto-generate sitemap for SEO
  - Endpoint: GET /sitemap.xml
  - Include all published blog posts

---

## Other Future Enhancements

_(Add additional future features here as they are identified)_

**Media Management Issues:**
- We can't edit program images - can only add when creating
- Same scenario for events as well
- Need a way to properly handle the singleton nature of meditation programs
- Blog post image editing is also not yet implemented (PATCH endpoint needed)