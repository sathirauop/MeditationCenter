# Blog Feature - Frontend Implementation Specification

This document provides complete specifications for building the frontend for the Meditation Center blog feature.

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication & Permissions](#authentication--permissions)
3. [Public Endpoints](#public-endpoints)
4. [Admin Endpoints](#admin-endpoints)
5. [Data Models](#data-models)
6. [Image Handling](#image-handling)
7. [Bilingual Support](#bilingual-support)
8. [Error Handling](#error-handling)
9. [Implementation Checklist](#implementation-checklist)

---

## Overview

### Backend Technology
- **Framework**: Spring Boot 3.5.5
- **Database**: PostgreSQL with jOOQ
- **Storage**: Cloudflare R2 (S3-compatible)
- **Security**: JWT-based authentication

### Blog Feature Scope
The blog supports:
- ✅ Bilingual content (English/Sinhala)
- ✅ Tag categorization
- ✅ Image galleries (cover + multiple gallery images)
- ✅ SEO metadata
- ✅ Draft/Published workflow
- ✅ View count tracking
- ✅ Advanced filtering and search
- ⚠️ **Note**: Blog post editing (PATCH) is not yet implemented - posts can only be created and published/unpublished

---

## Authentication & Permissions

### Public Endpoints
**No authentication required** for:
- GET /api/blog (list posts)
- GET /api/blog/{slug} (single post)
- GET /api/blog/tags (tags)

### Admin Endpoints
**Require JWT authentication** with specific permissions:

| Permission | Endpoints |
|------------|-----------|
| `VIEW_BLOG_POSTS` | GET /api/admin/blog, GET /api/admin/blog/{postId} |
| `CREATE_BLOG_POST` | POST /api/admin/blog |
| `PUBLISH_BLOG_POST` | POST /api/admin/blog/{postId}/publish, POST /api/admin/blog/{postId}/unpublish |
| `DELETE_BLOG_POST` | DELETE /api/admin/blog/{postId} |
| `MANAGE_BLOG_TAGS` | All tag endpoints |

### Authentication Header
```http
Authorization: Bearer {jwt_token}
```

---

## Public Endpoints

### 1. List Published Blog Posts

**Endpoint**: `GET /api/blog`

**Query Parameters**:
```typescript
interface GetBlogPostsParams {
  limit?: number;      // 1-100, default: 20
  offset?: number;     // 0-based, default: 0
  tagIds?: number[];   // Filter by tag IDs
  search?: string;     // Search in title/content
  startDate?: string;  // ISO date (e.g., "2024-01-01")
  endDate?: string;    // ISO date
  sortBy?: 'NEWEST' | 'OLDEST' | 'MOST_VIEWED'; // Default: NEWEST
}
```

**Request Example**:
```http
GET /api/blog?limit=10&offset=0&sortBy=NEWEST&tagIds=1,2&search=meditation
```

**Response**:
```json
{
  "data": [
    {
      "post_id": 1,
      "title": "Introduction to Mindfulness Meditation",
      "excerpt": "Learn the basics of mindfulness meditation...",
      "title_si": "සතිමත් භාවනාව පිළිබඳ හැඳින්වීම",
      "excerpt_si": "සතිමත් භාවනාවේ මූලික කරුණු ඉගෙන ගන්න...",
      "slug": "introduction-to-mindfulness-meditation",
      "author_name": "Bhante Rahula",
      "cover_image_url": "https://r2.example.com/blog-posts/1/cover/abc123.jpg?X-Amz-Expires=300...",
      "published_at": "2024-01-15T10:30:00",
      "view_count": 1250,
      "tag_names": ["Meditation", "Mindfulness", "Beginners"]
    }
  ],
  "currentOffset": 0,
  "maxOffset": 45
}
```

**Frontend Implementation Notes**:
- Use `currentOffset` and `maxOffset` for pagination UI
- Image URLs are **presigned** with 5-minute expiry - cache them but refresh if expired
- `tag_names` is an array of strings (not objects)
- Both English and Sinhala fields may be null - handle gracefully
- Display excerpt on list view, full content on detail view

---

### 2. Get Single Published Post

**Endpoint**: `GET /api/blog/{slug}`

**Path Parameters**:
- `slug`: URL-friendly post identifier (e.g., "introduction-to-mindfulness-meditation")

**Request Example**:
```http
GET /api/blog/introduction-to-mindfulness-meditation
```

**Response**:
```json
{
  "post_id": 1,
  "title": "Introduction to Mindfulness Meditation",
  "excerpt": "Learn the basics of mindfulness meditation...",
  "content": "# Full Markdown Content\n\nMindfulness meditation is...",
  "title_si": "සතිමත් භාවනාව පිළිබඳ හැඳින්වීම",
  "excerpt_si": "සතිමත් භාවනාවේ මූලික කරුණු ඉගෙන ගන්න...",
  "content_si": "# සම්පූර්ණ අන්තර්ගතය\n\nසතිමත් භාවනාව යනු...",
  "slug": "introduction-to-mindfulness-meditation",
  "author_name": "Bhante Rahula",
  "cover_image_url": "https://r2.example.com/blog-posts/1/cover/abc123.jpg?...",
  "gallery_image_urls": [
    "https://r2.example.com/blog-posts/1/gallery/def456.jpg?...",
    "https://r2.example.com/blog-posts/1/gallery/ghi789.jpg?..."
  ],
  "published_at": "2024-01-15T10:30:00",
  "view_count": 1251,
  "tag_names": ["Meditation", "Mindfulness", "Beginners"],
  "meta_title": "Mindfulness Meditation Guide | Isipathana Center",
  "meta_description": "Complete guide to mindfulness meditation for beginners..."
}
```

**Frontend Implementation Notes**:
- **View count auto-increments** on each GET request - this is intentional
- `gallery_image_urls` is a Set (array) of presigned URLs
- `content` field contains full Markdown - render with Markdown parser
- Use `meta_title` and `meta_description` for SEO tags
- All image URLs expire after 5 minutes - consider lazy loading and refreshing
- Post not found returns 404

---

### 3. Get Public Tags

**Endpoint**: `GET /api/blog/tags`

**Request Example**:
```http
GET /api/blog/tags
```

**Response**:
```json
[
  {
    "tag_id": 1,
    "name": "Meditation",
    "name_si": "භාවනාව",
    "slug": "meditation",
    "post_count": 15
  },
  {
    "tag_id": 2,
    "name": "Mindfulness",
    "name_si": "සිහිය",
    "slug": "mindfulness",
    "post_count": 12
  },
  {
    "tag_id": 3,
    "name": "Dharma",
    "name_si": "ධර්මය",
    "slug": "dharma",
    "post_count": 8
  }
]
```

**Frontend Implementation Notes**:
- `post_count` only includes PUBLISHED posts (not drafts)
- Tags with 0 posts are still returned
- Use for tag filter UI, tag clouds, etc.
- Sorted alphabetically by name

---

## Admin Endpoints

### 4. List All Blog Posts (Admin)

**Endpoint**: `GET /api/admin/blog`

**Authentication**: Required (`VIEW_BLOG_POSTS` permission)

**Query Parameters**:
```typescript
interface GetAdminBlogPostsParams {
  limit?: number;           // 1-100, default: 20
  offset?: number;          // 0-based, default: 0
  status?: 'DRAFT' | 'PUBLISHED';  // Filter by status
  authorId?: number;        // Filter by author
  tagIds?: number[];        // Filter by tags
  search?: string;          // Search in title/content
}
```

**Request Example**:
```http
GET /api/admin/blog?limit=20&offset=0&status=DRAFT
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**:
```json
{
  "data": [
    {
      "post_id": 5,
      "title": "Work in Progress Article",
      "title_si": null,
      "slug": "work-in-progress-article",
      "author_id": 2,
      "author_name": "Admin User",
      "status": "DRAFT",
      "published_at": null,
      "view_count": 0,
      "tag_names": ["Meditation"],
      "created_at": "2024-01-20T14:30:00",
      "updated_at": "2024-01-20T15:45:00"
    }
  ],
  "currentOffset": 0,
  "maxOffset": 5
}
```

**Frontend Implementation Notes**:
- Shows ALL posts including drafts
- `published_at` is null for drafts
- Use `status` field to display draft badge
- Sort by `updated_at` descending (most recently edited first)
- Use for admin dashboard table

---

### 5. Get Single Post for Editing (Admin)

**Endpoint**: `GET /api/admin/blog/{postId}`

**Authentication**: Required (`VIEW_BLOG_POSTS` permission)

**Path Parameters**:
- `postId`: Numeric post ID (not slug)

**Request Example**:
```http
GET /api/admin/blog/5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**:
```json
{
  "post_id": 5,
  "title": "Work in Progress Article",
  "excerpt": "This is a draft...",
  "content": "# Draft Content\n\nThis article is not finished...",
  "title_si": null,
  "excerpt_si": null,
  "content_si": null,
  "slug": "work-in-progress-article",
  "author_id": 2,
  "author_name": "Admin User",
  "cover_image_key": "blog-posts/5/cover/abc123.jpg",
  "gallery_image_keys": ["blog-posts/5/gallery/def456.jpg"],
  "status": "DRAFT",
  "published_at": null,
  "meta_title": null,
  "meta_description": null,
  "view_count": 0,
  "tag_ids": [1, 3, 5],
  "version": 3,
  "created_at": "2024-01-20T14:30:00",
  "updated_at": "2024-01-20T15:45:00"
}
```

**Frontend Implementation Notes**:
- Returns **image KEYS** (not URLs) - these are R2 storage paths
- Returns **tag IDs** (not names) - use for multi-select dropdown
- `version` field is for optimistic locking (future PATCH implementation)
- All Sinhala fields may be null
- Use this endpoint to populate edit forms
- **IMPORTANT**: Currently there is no PATCH endpoint to save edits - this is for future implementation

---

### 6. Create Blog Post (Admin)

**Endpoint**: `POST /api/admin/blog`

**Authentication**: Required (`CREATE_BLOG_POST` permission)

**Content-Type**: `multipart/form-data`

**Form Parts**:

1. **`request`** (application/json):
```json
{
  "title": "New Blog Post",
  "excerpt": "Short summary...",
  "content": "# Full Content\n\nMarkdown formatted content...",
  "titleSi": "නව බ්ලොග් ලිපිය",
  "excerptSi": "කෙටි සාරාංශය...",
  "contentSi": "# සම්පූර්ණ අන්තර්ගතය\n\n...",
  "slug": "new-blog-post",
  "metaTitle": "New Blog Post | Isipathana Center",
  "metaDescription": "Learn about...",
  "tagIds": [1, 2, 3],
  "status": "DRAFT"
}
```

2. **`coverImage`** (image file, optional):
   - File types: JPG, PNG, WebP
   - Max size: Check with backend validation

3. **`galleryImages`** (multiple image files, optional):
   - Array of image files
   - Same format restrictions as cover image

**Request Example (JavaScript)**:
```javascript
const formData = new FormData();

// Add JSON request part
const request = {
  title: "New Blog Post",
  excerpt: "Short summary...",
  content: "# Full Content...",
  tagIds: [1, 2, 3],
  status: "DRAFT"
};
formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));

// Add images
formData.append('coverImage', coverImageFile);
galleryImageFiles.forEach(file => {
  formData.append('galleryImages', file);
});

// Send request
fetch('/api/admin/blog', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});
```

**Response** (201 Created):
```json
{
  "post_id": 10,
  "title": "New Blog Post",
  "excerpt": "Short summary...",
  "content": "# Full Content...",
  "title_si": "නව බ්ලොග් ලිපිය",
  "excerpt_si": "කෙටි සාරාංශය...",
  "content_si": "# සම්පූර්ණ අන්තර්ගතය...",
  "slug": "new-blog-post",
  "author_id": 2,
  "author_name": "Admin User",
  "cover_image_key": "blog-posts/10/cover/abc123.jpg",
  "gallery_image_keys": ["blog-posts/10/gallery/def456.jpg"],
  "status": "DRAFT",
  "published_at": null,
  "meta_title": "New Blog Post | Isipathana Center",
  "meta_description": "Learn about...",
  "view_count": 0,
  "tag_ids": [1, 2, 3],
  "version": 1,
  "created_at": "2024-01-20T16:00:00",
  "updated_at": "2024-01-20T16:00:00"
}
```

**Validation Rules**:
- `title`: Required, 1-500 chars
- `content`: Required, min 10 chars
- `slug`: Optional (auto-generated if not provided), must match pattern: `^[a-z0-9]+(?:-[a-z0-9]+)*$`
- `excerpt`: Optional, max 500 chars
- `metaTitle`: Optional, max 255 chars
- `metaDescription`: Optional, max 500 chars
- `tagIds`: Optional, must be valid tag IDs
- `status`: Optional, defaults to "DRAFT"

**Frontend Implementation Notes**:
- If `slug` is not provided, backend auto-generates from title
- If slug already exists, backend appends "-2", "-3", etc.
- Images are uploaded to R2 storage automatically
- Returns image keys (not URLs) in response
- Creates post as DRAFT by default
- Use "Save as Draft" button to create with status="DRAFT"

---

### 7. Publish Blog Post (Admin)

**Endpoint**: `POST /api/admin/blog/{postId}/publish`

**Authentication**: Required (`PUBLISH_BLOG_POST` permission)

**Request Example**:
```http
POST /api/admin/blog/5/publish
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response** (200 OK):
```
(Empty body)
```

**Frontend Implementation Notes**:
- Changes status from DRAFT → PUBLISHED
- Sets `published_at` to current timestamp
- Validates post has required fields (title, content)
- Returns 400 if post is already published
- Returns 404 if post not found
- Use for "Publish" button in admin UI

---

### 8. Unpublish Blog Post (Admin)

**Endpoint**: `POST /api/admin/blog/{postId}/unpublish`

**Authentication**: Required (`PUBLISH_BLOG_POST` permission)

**Request Example**:
```http
POST /api/admin/blog/5/unpublish
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response** (200 OK):
```
(Empty body)
```

**Frontend Implementation Notes**:
- Changes status from PUBLISHED → DRAFT
- Post becomes invisible on public blog
- Use for "Unpublish" or "Revert to Draft" button

---

### 9. Delete Blog Post (Admin)

**Endpoint**: `DELETE /api/admin/blog/{postId}`

**Authentication**: Required (`DELETE_BLOG_POST` permission)

**Request Example**:
```http
DELETE /api/admin/blog/5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response** (204 No Content):
```
(Empty body)
```

**Frontend Implementation Notes**:
- **Soft delete** - sets `deleted_at` timestamp, doesn't physically remove
- Post becomes invisible everywhere (public + admin)
- Cannot be undone from UI (database restoration only)
- Show confirmation dialog before deleting

---

### 10. List All Tags (Admin)

**Endpoint**: `GET /api/admin/blog/tags`

**Authentication**: Required (`MANAGE_BLOG_TAGS` permission)

**Request Example**:
```http
GET /api/admin/blog/tags
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**:
```json
[
  {
    "tag_id": 1,
    "name": "Meditation",
    "name_si": "භාවනාව",
    "slug": "meditation",
    "created_at": "2024-01-01T10:00:00",
    "post_count": 15
  }
]
```

**Frontend Implementation Notes**:
- Same as public endpoint but requires authentication
- `post_count` includes ALL posts (drafts + published)
- Use for tag management dashboard

---

### 11. Create Tag (Admin)

**Endpoint**: `POST /api/admin/blog/tags`

**Authentication**: Required (`MANAGE_BLOG_TAGS` permission)

**Request Body**:
```json
{
  "name": "Vipassana",
  "nameSi": "විදර්ශනා",
  "slug": "vipassana"
}
```

**Response** (201 Created):
```json
{
  "tag_id": 10,
  "name": "Vipassana",
  "name_si": "විදර්ශනා",
  "slug": "vipassana",
  "created_at": "2024-01-20T16:30:00"
}
```

**Validation Rules**:
- `name`: Required, 2-50 chars
- `nameSi`: Optional, 2-50 chars
- `slug`: Optional (auto-generated if not provided), pattern: `^[a-z0-9]+(?:-[a-z0-9]+)*$`

**Frontend Implementation Notes**:
- If slug not provided, auto-generated from name
- Slug must be unique (returns 400 if duplicate)
- Name must be unique (returns 400 if duplicate)

---

### 12. Update Tag (Admin)

**Endpoint**: `PATCH /api/admin/blog/tags/{tagId}`

**Authentication**: Required (`MANAGE_BLOG_TAGS` permission)

**Request Body** (all fields optional):
```json
{
  "name": "Updated Name",
  "nameSi": "යාවත්කාලීන නම",
  "slug": "updated-slug"
}
```

**Response** (200 OK):
```json
{
  "tag_id": 10,
  "name": "Updated Name",
  "name_si": "යාවත්කාලීන නම",
  "slug": "updated-slug",
  "created_at": "2024-01-20T16:30:00"
}
```

**Frontend Implementation Notes**:
- Only provided fields are updated (partial update)
- Slug uniqueness validated if slug is provided
- Name uniqueness validated if name is provided

---

### 13. Delete Tag (Admin)

**Endpoint**: `DELETE /api/admin/blog/tags/{tagId}`

**Authentication**: Required (`MANAGE_BLOG_TAGS` permission)

**Request Example**:
```http
DELETE /api/admin/blog/tags/10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response** (204 No Content):
```
(Empty body)
```

**Frontend Implementation Notes**:
- **Hard delete** - permanently removes tag
- Removes tag from all associated posts (blog_post_tags junction table)
- Posts are not deleted, just lose this tag
- Show confirmation dialog

---

## Data Models

### BlogPost (Public)
```typescript
interface BlogPost {
  post_id: number;
  title: string;
  excerpt: string | null;
  content: string;  // Markdown
  title_si: string | null;
  excerpt_si: string | null;
  content_si: string | null;
  slug: string;
  author_name: string;
  cover_image_url: string | null;  // Presigned URL
  gallery_image_urls: string[];    // Presigned URLs
  published_at: string;  // ISO 8601
  view_count: number;
  tag_names: string[];
  meta_title: string | null;
  meta_description: string | null;
}
```

### BlogPost (Admin)
```typescript
interface AdminBlogPost {
  post_id: number;
  title: string;
  excerpt: string | null;
  content: string;  // Markdown
  title_si: string | null;
  excerpt_si: string | null;
  content_si: string | null;
  slug: string;
  author_id: number;
  author_name: string;
  cover_image_key: string | null;  // R2 storage key
  gallery_image_keys: string[];    // R2 storage keys
  status: 'DRAFT' | 'PUBLISHED';
  published_at: string | null;  // ISO 8601
  meta_title: string | null;
  meta_description: string | null;
  view_count: number;
  tag_ids: number[];  // Not tag names
  version: number;    // Optimistic locking
  created_at: string;  // ISO 8601
  updated_at: string;  // ISO 8601
}
```

### BlogTag
```typescript
interface BlogTag {
  tag_id: number;
  name: string;
  name_si: string | null;
  slug: string;
  post_count: number;  // Public: PUBLISHED only, Admin: all posts
  created_at?: string;  // Only in admin response
}
```

### Pagination Response
```typescript
interface PaginatedResponse<T> {
  data: T[];
  currentOffset: number;
  maxOffset: number;  // Total count
}
```

---

## Image Handling

### Public Endpoints (Presigned URLs)
- Public endpoints return **presigned URLs** that expire in **5 minutes**
- Format: `https://r2.example.com/blog-posts/1/cover/abc123.jpg?X-Amz-Algorithm=...&X-Amz-Expires=300...`

**Frontend Best Practices**:
```typescript
// Cache presigned URLs but handle expiry
interface CachedImage {
  url: string;
  fetchedAt: number;
}

function shouldRefreshImage(cached: CachedImage): boolean {
  const EXPIRY_MS = 4 * 60 * 1000; // 4 minutes (before 5min expiry)
  return Date.now() - cached.fetchedAt > EXPIRY_MS;
}

// Lazy load gallery images
<img
  src={imageUrl}
  loading="lazy"
  onError={(e) => {
    // Retry fetching post to get fresh presigned URL
    refreshPost();
  }}
/>
```

### Admin Endpoints (Storage Keys)
- Admin endpoints return **R2 storage keys** (not URLs)
- Format: `blog-posts/5/cover/abc123.jpg`
- These are internal references, not accessible URLs

**To display images in admin UI**:
```typescript
// Option 1: Fetch the public post endpoint (if published)
GET /api/blog/{slug}  // Returns presigned URLs

// Option 2: Construct R2 URL (if you have R2 public URL)
const imageUrl = `https://your-r2-domain.com/${imageKey}`;

// Option 3: Wait for PATCH endpoint to be implemented
// which should return presigned URLs in response
```

### Image Upload Guidelines
- **Supported formats**: JPG, PNG, WebP
- **Max file size**: Check backend validation (typically 5-10MB)
- **Cover image**: Single file, optional
- **Gallery images**: Multiple files, optional

**Validation Example**:
```typescript
function validateImage(file: File): string | null {
  const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
  if (!validTypes.includes(file.type)) {
    return 'Invalid file type. Use JPG, PNG, or WebP.';
  }

  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    return 'File too large. Maximum 10MB.';
  }

  return null; // Valid
}
```

---

## Bilingual Support

### Language Fields
Every text field has an optional Sinhala equivalent:
- `title` ↔ `title_si`
- `excerpt` ↔ `excerpt_si`
- `content` ↔ `content_si`
- `tag.name` ↔ `tag.name_si`

### Implementation Patterns

**Display Logic**:
```typescript
interface LanguageContext {
  lang: 'en' | 'si';
}

function getLocalizedField(
  post: BlogPost,
  field: 'title' | 'excerpt' | 'content',
  lang: LanguageContext['lang']
): string {
  if (lang === 'si') {
    const siField = `${field}_si` as keyof BlogPost;
    return post[siField] || post[field]; // Fallback to English
  }
  return post[field];
}
```

**Form Handling**:
```tsx
<form>
  {/* English Section */}
  <fieldset>
    <legend>English Content</legend>
    <input name="title" required />
    <textarea name="excerpt" />
    <textarea name="content" required />
  </fieldset>

  {/* Sinhala Section (Optional) */}
  <fieldset>
    <legend>සිංහල අන්තර්ගතය (Optional)</legend>
    <input name="titleSi" />
    <textarea name="excerptSi" />
    <textarea name="contentSi" />
  </fieldset>
</form>
```

**Language Switching**:
```tsx
function BlogPost({ post, lang }: { post: BlogPost, lang: 'en' | 'si' }) {
  const title = lang === 'si' && post.title_si ? post.title_si : post.title;
  const content = lang === 'si' && post.content_si ? post.content_si : post.content;

  return (
    <article>
      <h1>{title}</h1>
      <ReactMarkdown>{content}</ReactMarkdown>
    </article>
  );
}
```

---

## Error Handling

### HTTP Status Codes

| Code | Meaning | Common Causes |
|------|---------|---------------|
| 200 | OK | Request succeeded |
| 201 | Created | POST request created resource |
| 204 | No Content | DELETE succeeded |
| 400 | Bad Request | Validation error, invalid data |
| 401 | Unauthorized | Missing/invalid JWT token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate slug/name, version conflict |

### Error Response Format
```json
{
  "timestamp": "2024-01-20T16:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: title is required",
  "path": "/api/admin/blog"
}
```

### Common Error Scenarios

**1. Validation Errors (400)**:
```json
{
  "message": "Validation failed: title must be between 1 and 500 characters"
}
```
Frontend: Display field-specific validation errors

**2. Authentication Errors (401)**:
```json
{
  "message": "JWT token is invalid or expired"
}
```
Frontend: Redirect to login, clear local storage

**3. Permission Errors (403)**:
```json
{
  "message": "Access denied: missing required authority 'CREATE_BLOG_POST'"
}
```
Frontend: Show "Access Denied" message, hide restricted features

**4. Not Found (404)**:
```json
{
  "message": "Blog post not found: introduction-to-meditation"
}
```
Frontend: Show 404 page, suggest similar posts

**5. Duplicate Slug (409)**:
```json
{
  "message": "Blog post with slug 'meditation-guide' already exists"
}
```
Frontend: Suggest alternative slug, append timestamp

**Error Handling Example**:
```typescript
async function createBlogPost(data: CreatePostRequest): Promise<BlogPost> {
  try {
    const response = await fetch('/api/admin/blog', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${getToken()}`,
        'Content-Type': 'multipart/form-data'
      },
      body: data
    });

    if (!response.ok) {
      const error = await response.json();

      switch (response.status) {
        case 400:
          throw new ValidationError(error.message);
        case 401:
          redirectToLogin();
          throw new AuthError('Please log in');
        case 403:
          throw new PermissionError('Insufficient permissions');
        case 409:
          throw new ConflictError(error.message);
        default:
          throw new Error(error.message);
      }
    }

    return await response.json();
  } catch (error) {
    console.error('Failed to create post:', error);
    throw error;
  }
}
```

---

## Implementation Checklist

### Public Blog Pages

**1. Blog List Page** (`/blog`)
- [ ] Fetch posts from GET /api/blog
- [ ] Display post cards with:
  - [ ] Title (bilingual)
  - [ ] Excerpt (bilingual)
  - [ ] Cover image (presigned URL)
  - [ ] Author name
  - [ ] Published date
  - [ ] Tags
  - [ ] View count
- [ ] Implement pagination (offset-based)
- [ ] Add filters:
  - [ ] Tag filter (multi-select)
  - [ ] Search box
  - [ ] Date range picker
  - [ ] Sort dropdown (Newest/Oldest/Most Viewed)
- [ ] Language switcher (EN/SI)
- [ ] Handle image expiry/refresh
- [ ] SEO metadata

**2. Single Post Page** (`/blog/{slug}`)
- [ ] Fetch post from GET /api/blog/{slug}
- [ ] Display full post:
  - [ ] Title (bilingual)
  - [ ] Author and published date
  - [ ] Cover image
  - [ ] Full content (Markdown rendered)
  - [ ] Gallery images (lazy loaded)
  - [ ] Tags (clickable)
  - [ ] View count
- [ ] Social share buttons
- [ ] Related posts (by tags)
- [ ] Language switcher
- [ ] SEO: Use meta_title and meta_description
- [ ] Handle 404 gracefully
- [ ] Handle image expiry

**3. Tag Filter/Cloud**
- [ ] Fetch tags from GET /api/blog/tags
- [ ] Display tag cloud or filter UI
- [ ] Show post count per tag
- [ ] Link to filtered blog list

---

### Admin Pages

**4. Admin Blog Dashboard** (`/admin/blog`)
- [ ] Fetch posts from GET /api/admin/blog
- [ ] Display posts table with:
  - [ ] Title
  - [ ] Status badge (Draft/Published)
  - [ ] Author
  - [ ] Tags
  - [ ] View count
  - [ ] Created/Updated dates
  - [ ] Actions (Edit/Publish/Delete)
- [ ] Filters:
  - [ ] Status filter (All/Draft/Published)
  - [ ] Author filter
  - [ ] Tag filter
  - [ ] Search
- [ ] Pagination
- [ ] "New Post" button
- [ ] Bulk actions (future)

**5. Create Post Page** (`/admin/blog/new`)
- [ ] Form with fields:
  - [ ] Title (required, English)
  - [ ] Title (optional, Sinhala)
  - [ ] Excerpt (optional, English)
  - [ ] Excerpt (optional, Sinhala)
  - [ ] Content editor (required, English, Markdown)
  - [ ] Content editor (optional, Sinhala, Markdown)
  - [ ] Slug (optional, auto-generated)
  - [ ] Cover image upload (optional)
  - [ ] Gallery images upload (optional, multiple)
  - [ ] Tag selector (multi-select)
  - [ ] Meta title (optional)
  - [ ] Meta description (optional)
- [ ] Buttons:
  - [ ] "Save as Draft" (status=DRAFT)
  - [ ] "Save and Publish" (creates as DRAFT then publishes)
  - [ ] "Cancel"
- [ ] Multipart form submission
- [ ] Image preview
- [ ] Validation errors display
- [ ] Success/error notifications
- [ ] Redirect to post list on success

**6. View/Edit Post Page** (`/admin/blog/{postId}`)
**Note**: Editing (PATCH) not yet implemented on backend
- [ ] Fetch post from GET /api/admin/blog/{postId}
- [ ] Display read-only view with:
  - [ ] All post fields
  - [ ] Status indicator
  - [ ] Version number
  - [ ] Image keys (display note: "Editing not yet available")
- [ ] Action buttons:
  - [ ] "Publish" (if DRAFT)
  - [ ] "Unpublish" (if PUBLISHED)
  - [ ] "Delete" (with confirmation)
  - [ ] "Edit" (disabled with tooltip: "Coming soon")
- [ ] Handle publish/unpublish actions
- [ ] Delete confirmation dialog
- [ ] Success/error notifications

**7. Tag Management** (`/admin/blog/tags`)
- [ ] Fetch tags from GET /api/admin/blog/tags
- [ ] Display tags table:
  - [ ] Name (EN/SI)
  - [ ] Slug
  - [ ] Post count
  - [ ] Created date
  - [ ] Actions (Edit/Delete)
- [ ] "New Tag" button
- [ ] Create tag modal:
  - [ ] Name (required, English)
  - [ ] Name (optional, Sinhala)
  - [ ] Slug (optional, auto-generated)
- [ ] Edit tag modal (PATCH)
- [ ] Delete confirmation
- [ ] Validation errors
- [ ] Success/error notifications

---

### Technical Implementation

**8. API Client**
- [ ] Create typed API client with all endpoints
- [ ] JWT token management
- [ ] Automatic token refresh
- [ ] Error interceptors
- [ ] Loading states

**9. State Management**
- [ ] Blog posts state
- [ ] Tags state
- [ ] Current user/permissions
- [ ] Language preference
- [ ] Pagination state
- [ ] Filters state

**10. Image Handling**
- [ ] Presigned URL caching (4-minute expiry)
- [ ] Lazy loading for gallery images
- [ ] Image upload preview
- [ ] File validation (type, size)
- [ ] Compression before upload (optional)
- [ ] Handle upload errors

**11. Markdown Rendering**
- [ ] Install Markdown parser (react-markdown, marked, etc.)
- [ ] Syntax highlighting for code blocks
- [ ] Safe HTML rendering (sanitize)
- [ ] Custom styling for headings, lists, etc.

**12. SEO**
- [ ] Dynamic meta tags (title, description)
- [ ] Open Graph tags (og:title, og:image, etc.)
- [ ] Twitter Card tags
- [ ] Canonical URLs
- [ ] JSON-LD structured data (BlogPosting schema)

**13. Accessibility**
- [ ] Semantic HTML (article, nav, section)
- [ ] Alt text for images
- [ ] ARIA labels for buttons/links
- [ ] Keyboard navigation
- [ ] Screen reader support

**14. Performance**
- [ ] Image lazy loading
- [ ] Pagination (avoid loading all posts)
- [ ] Code splitting (route-based)
- [ ] Caching strategy
- [ ] Debounce search input

---

## Example API Client Implementation

```typescript
// api/blog.ts
import { getAuthToken } from './auth';

const API_BASE = '/api';

interface PaginatedResponse<T> {
  data: T[];
  currentOffset: number;
  maxOffset: number;
}

// Public API
export const blogApi = {
  // List published posts
  async listPosts(params?: {
    limit?: number;
    offset?: number;
    tagIds?: number[];
    search?: string;
    startDate?: string;
    endDate?: string;
    sortBy?: 'NEWEST' | 'OLDEST' | 'MOST_VIEWED';
  }): Promise<PaginatedResponse<BlogPost>> {
    const query = new URLSearchParams();
    if (params?.limit) query.set('limit', params.limit.toString());
    if (params?.offset) query.set('offset', params.offset.toString());
    if (params?.tagIds) query.set('tagIds', params.tagIds.join(','));
    if (params?.search) query.set('search', params.search);
    if (params?.startDate) query.set('startDate', params.startDate);
    if (params?.endDate) query.set('endDate', params.endDate);
    if (params?.sortBy) query.set('sortBy', params.sortBy);

    const response = await fetch(`${API_BASE}/blog?${query}`);
    if (!response.ok) throw new Error('Failed to fetch posts');
    return response.json();
  },

  // Get single post
  async getPost(slug: string): Promise<BlogPost> {
    const response = await fetch(`${API_BASE}/blog/${slug}`);
    if (!response.ok) {
      if (response.status === 404) throw new Error('Post not found');
      throw new Error('Failed to fetch post');
    }
    return response.json();
  },

  // Get tags
  async getTags(): Promise<BlogTag[]> {
    const response = await fetch(`${API_BASE}/blog/tags`);
    if (!response.ok) throw new Error('Failed to fetch tags');
    return response.json();
  }
};

// Admin API
export const adminBlogApi = {
  // List all posts (admin)
  async listPosts(params?: {
    limit?: number;
    offset?: number;
    status?: 'DRAFT' | 'PUBLISHED';
    authorId?: number;
    tagIds?: number[];
    search?: string;
  }): Promise<PaginatedResponse<AdminBlogPost>> {
    const query = new URLSearchParams();
    if (params?.limit) query.set('limit', params.limit.toString());
    if (params?.offset) query.set('offset', params.offset.toString());
    if (params?.status) query.set('status', params.status);
    if (params?.authorId) query.set('authorId', params.authorId.toString());
    if (params?.tagIds) query.set('tagIds', params.tagIds.join(','));
    if (params?.search) query.set('search', params.search);

    const response = await fetch(`${API_BASE}/admin/blog?${query}`, {
      headers: { 'Authorization': `Bearer ${getAuthToken()}` }
    });
    if (!response.ok) throw new Error('Failed to fetch admin posts');
    return response.json();
  },

  // Get single post for editing
  async getPost(postId: number): Promise<AdminBlogPost> {
    const response = await fetch(`${API_BASE}/admin/blog/${postId}`, {
      headers: { 'Authorization': `Bearer ${getAuthToken()}` }
    });
    if (!response.ok) throw new Error('Failed to fetch post');
    return response.json();
  },

  // Create post
  async createPost(
    request: CreatePostRequest,
    coverImage?: File,
    galleryImages?: File[]
  ): Promise<AdminBlogPost> {
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));
    if (coverImage) formData.append('coverImage', coverImage);
    if (galleryImages) {
      galleryImages.forEach(img => formData.append('galleryImages', img));
    }

    const response = await fetch(`${API_BASE}/admin/blog`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getAuthToken()}` },
      body: formData
    });
    if (!response.ok) throw new Error('Failed to create post');
    return response.json();
  },

  // Publish post
  async publishPost(postId: number): Promise<void> {
    const response = await fetch(`${API_BASE}/admin/blog/${postId}/publish`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getAuthToken()}` }
    });
    if (!response.ok) throw new Error('Failed to publish post');
  },

  // Unpublish post
  async unpublishPost(postId: number): Promise<void> {
    const response = await fetch(`${API_BASE}/admin/blog/${postId}/unpublish`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getAuthToken()}` }
    });
    if (!response.ok) throw new Error('Failed to unpublish post');
  },

  // Delete post
  async deletePost(postId: number): Promise<void> {
    const response = await fetch(`${API_BASE}/admin/blog/${postId}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getAuthToken()}` }
    });
    if (!response.ok) throw new Error('Failed to delete post');
  },

  // Tag management
  tags: {
    async list(): Promise<BlogTag[]> {
      const response = await fetch(`${API_BASE}/admin/blog/tags`, {
        headers: { 'Authorization': `Bearer ${getAuthToken()}` }
      });
      if (!response.ok) throw new Error('Failed to fetch tags');
      return response.json();
    },

    async create(data: { name: string; nameSi?: string; slug?: string }): Promise<BlogTag> {
      const response = await fetch(`${API_BASE}/admin/blog/tags`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${getAuthToken()}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });
      if (!response.ok) throw new Error('Failed to create tag');
      return response.json();
    },

    async update(tagId: number, data: Partial<{ name: string; nameSi: string; slug: string }>): Promise<BlogTag> {
      const response = await fetch(`${API_BASE}/admin/blog/tags/${tagId}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${getAuthToken()}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });
      if (!response.ok) throw new Error('Failed to update tag');
      return response.json();
    },

    async delete(tagId: number): Promise<void> {
      const response = await fetch(`${API_BASE}/admin/blog/tags/${tagId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${getAuthToken()}` }
      });
      if (!response.ok) throw new Error('Failed to delete tag');
    }
  }
};

// Type definitions
export interface BlogPost {
  post_id: number;
  title: string;
  excerpt: string | null;
  content: string;
  title_si: string | null;
  excerpt_si: string | null;
  content_si: string | null;
  slug: string;
  author_name: string;
  cover_image_url: string | null;
  gallery_image_urls: string[];
  published_at: string;
  view_count: number;
  tag_names: string[];
  meta_title: string | null;
  meta_description: string | null;
}

export interface AdminBlogPost extends Omit<BlogPost, 'cover_image_url' | 'gallery_image_urls' | 'tag_names' | 'author_name'> {
  author_id: number;
  author_name: string;
  cover_image_key: string | null;
  gallery_image_keys: string[];
  status: 'DRAFT' | 'PUBLISHED';
  published_at: string | null;
  tag_ids: number[];
  version: number;
  created_at: string;
  updated_at: string;
}

export interface BlogTag {
  tag_id: number;
  name: string;
  name_si: string | null;
  slug: string;
  post_count: number;
  created_at?: string;
}

export interface CreatePostRequest {
  title: string;
  excerpt?: string;
  content: string;
  titleSi?: string;
  excerptSi?: string;
  contentSi?: string;
  slug?: string;
  metaTitle?: string;
  metaDescription?: string;
  tagIds?: number[];
  status?: 'DRAFT' | 'PUBLISHED';
}
```

---

## Quick Reference

### Base URL
```
http://localhost:8080/api
```

### Public Endpoints (No Auth)
```
GET    /blog                   - List published posts
GET    /blog/{slug}           - Single published post
GET    /blog/tags             - Public tags
```

### Admin Endpoints (Auth Required)
```
GET    /admin/blog                      - List all posts
GET    /admin/blog/{postId}             - Get post for editing
POST   /admin/blog                      - Create post (multipart)
POST   /admin/blog/{postId}/publish     - Publish post
POST   /admin/blog/{postId}/unpublish   - Unpublish post
DELETE /admin/blog/{postId}             - Delete post
GET    /admin/blog/tags                 - List tags
POST   /admin/blog/tags                 - Create tag
PATCH  /admin/blog/tags/{tagId}         - Update tag
DELETE /admin/blog/tags/{tagId}         - Delete tag
```

### Not Yet Implemented
```
PATCH  /admin/blog/{postId}                  - Update post (TODO)
PATCH  /admin/blog/drafts/{postId}           - Auto-save draft (TODO)
```

---

## Support & Questions

**Backend Implementation**: Complete (13/15 endpoints)
**Technology**: Spring Boot 3.5.5, PostgreSQL, jOOQ, Cloudflare R2
**Build Status**: ✅ SUCCESS
**Documentation**: Complete

For questions about:
- **API behavior**: Test endpoints with Postman/curl
- **Validation rules**: Check error messages in 400 responses
- **Permissions**: See authentication section above
- **Image handling**: See image handling section above

---

**Last Updated**: 2024-01-20
**Backend Version**: Phases 1-6 Complete
**Remaining Work**: PATCH endpoints (documented in FUTURE.md)
