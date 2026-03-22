-- V18: Create Gallery Feature
-- Description: Creates tables for gallery management with groups and photos
-- Author: Sathira Basnayake
-- Date: 2026-03-09

-- =====================================================
-- 1. Gallery Groups Table
-- =====================================================
CREATE TABLE gallery_groups (
    group_id BIGSERIAL PRIMARY KEY,

    -- Content (bilingual)
    name VARCHAR(255) NOT NULL,
    name_si VARCHAR(255),

    -- Display
    sort_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comments
COMMENT ON TABLE gallery_groups IS 'Gallery groups (albums) for organizing photos';
COMMENT ON COLUMN gallery_groups.name IS 'Group name in English';
COMMENT ON COLUMN gallery_groups.name_si IS 'Group name in Sinhala';
COMMENT ON COLUMN gallery_groups.sort_order IS 'Display order (lower = first)';
COMMENT ON COLUMN gallery_groups.active IS 'Whether the group is visible on the public site';

-- Indexes
CREATE INDEX idx_gallery_groups_sort_order ON gallery_groups(sort_order);
CREATE INDEX idx_gallery_groups_active ON gallery_groups(active) WHERE active = true;

-- =====================================================
-- 2. Gallery Photos Table
-- =====================================================
CREATE TABLE gallery_photos (
    photo_id BIGSERIAL PRIMARY KEY,

    -- Parent group
    group_id BIGINT NOT NULL,

    -- Image (R2 storage key)
    image_key VARCHAR(255) NOT NULL,

    -- Content (bilingual)
    caption VARCHAR(500),
    caption_si VARCHAR(500),

    -- Display
    sort_order INTEGER NOT NULL DEFAULT 0,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_gallery_photos_group
        FOREIGN KEY (group_id) REFERENCES gallery_groups(group_id) ON DELETE CASCADE
);

-- Comments
COMMENT ON TABLE gallery_photos IS 'Photos belonging to gallery groups';
COMMENT ON COLUMN gallery_photos.group_id IS 'Reference to the parent gallery group';
COMMENT ON COLUMN gallery_photos.image_key IS 'R2 storage key for the photo';
COMMENT ON COLUMN gallery_photos.caption IS 'Optional caption in English';
COMMENT ON COLUMN gallery_photos.caption_si IS 'Optional caption in Sinhala';
COMMENT ON COLUMN gallery_photos.sort_order IS 'Display order within the group (lower = first)';

-- Indexes
CREATE INDEX idx_gallery_photos_group_id ON gallery_photos(group_id);
CREATE INDEX idx_gallery_photos_sort_order ON gallery_photos(group_id, sort_order);
