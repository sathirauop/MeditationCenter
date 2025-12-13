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

## Other Future Enhancements

_(Add additional future features here as they are identified)_



-- we cant edit the programs images / we can only add images when creating the program
-- same scenario for events as well
-- need a way to properly handle the singleton nature of the meditation programs