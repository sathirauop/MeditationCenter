-- V17: Create Blog Posts Feature
-- Description: Creates tables for blog post management with bilingual support (English/Sinhala)
-- Author: Sathira Basnayake
-- Date: 2025-12-18

-- =====================================================
-- 1. Blog Posts Table
-- =====================================================
CREATE TABLE blog_posts (
    post_id BIGSERIAL PRIMARY KEY,

    -- English content
    title VARCHAR(255) NOT NULL,
    excerpt TEXT,
    content TEXT NOT NULL,

    -- Sinhala content (bilingual support)
    title_si VARCHAR(255),
    excerpt_si TEXT,
    content_si TEXT,

    -- Metadata
    slug VARCHAR(255) NOT NULL UNIQUE,
    author_id BIGINT NOT NULL,

    -- Images (R2 storage keys)
    cover_image_key VARCHAR(255),
    image_keys TEXT[],  -- Array of additional image keys

    -- Publishing
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    published_at TIMESTAMP,

    -- SEO
    meta_title VARCHAR(255),
    meta_description TEXT,

    -- Analytics
    view_count BIGINT NOT NULL DEFAULT 0,

    -- Optimistic locking (for auto-save conflict resolution)
    version BIGINT NOT NULL DEFAULT 1,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,  -- Soft delete

    -- Foreign keys
    CONSTRAINT fk_blog_posts_author
        FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_blog_posts_status
        CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT chk_blog_posts_published_at
        CHECK (
            (status = 'PUBLISHED' AND published_at IS NOT NULL) OR
            (status != 'PUBLISHED')
        )
);

-- Comments
COMMENT ON TABLE blog_posts IS 'Blog posts with bilingual support and image management';
COMMENT ON COLUMN blog_posts.title IS 'Blog post title in English';
COMMENT ON COLUMN blog_posts.title_si IS 'Blog post title in Sinhala';
COMMENT ON COLUMN blog_posts.excerpt IS 'Short excerpt/summary in English';
COMMENT ON COLUMN blog_posts.excerpt_si IS 'Short excerpt/summary in Sinhala';
COMMENT ON COLUMN blog_posts.content IS 'Full blog post content in English';
COMMENT ON COLUMN blog_posts.content_si IS 'Full blog post content in Sinhala';
COMMENT ON COLUMN blog_posts.slug IS 'URL-friendly unique identifier (lowercase, hyphens only)';
COMMENT ON COLUMN blog_posts.author_id IS 'Reference to admin user who created the post';
COMMENT ON COLUMN blog_posts.cover_image_key IS 'R2 storage key for cover image';
COMMENT ON COLUMN blog_posts.image_keys IS 'Array of R2 storage keys for gallery images';
COMMENT ON COLUMN blog_posts.status IS 'Publication status: DRAFT, PUBLISHED, ARCHIVED';
COMMENT ON COLUMN blog_posts.published_at IS 'Timestamp when post was published';
COMMENT ON COLUMN blog_posts.meta_title IS 'SEO meta title (can differ from main title)';
COMMENT ON COLUMN blog_posts.meta_description IS 'SEO meta description';
COMMENT ON COLUMN blog_posts.view_count IS 'Number of times post was viewed';
COMMENT ON COLUMN blog_posts.version IS 'Version number for optimistic locking (auto-save conflict detection)';
COMMENT ON COLUMN blog_posts.deleted_at IS 'Soft delete timestamp (NULL = active, NOT NULL = deleted)';

-- Indexes
CREATE INDEX idx_blog_posts_slug ON blog_posts(slug);
CREATE INDEX idx_blog_posts_author_id ON blog_posts(author_id);
CREATE INDEX idx_blog_posts_status_published ON blog_posts(status, published_at DESC);
CREATE INDEX idx_blog_posts_created_at ON blog_posts(created_at DESC);
CREATE INDEX idx_blog_posts_deleted_at ON blog_posts(deleted_at) WHERE deleted_at IS NULL;

-- =====================================================
-- 2. Blog Tags Table
-- =====================================================
CREATE TABLE blog_tags (
    tag_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    name_si VARCHAR(50),  -- Sinhala tag name
    slug VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comments
COMMENT ON TABLE blog_tags IS 'Tags for categorizing blog posts (bilingual)';
COMMENT ON COLUMN blog_tags.name IS 'Tag name in English';
COMMENT ON COLUMN blog_tags.name_si IS 'Tag name in Sinhala';
COMMENT ON COLUMN blog_tags.slug IS 'URL-friendly unique identifier for the tag';

-- Indexes
CREATE INDEX idx_blog_tags_slug ON blog_tags(slug);

-- =====================================================
-- 3. Blog Post Tags Junction Table (Many-to-Many)
-- =====================================================
CREATE TABLE blog_post_tags (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),

    CONSTRAINT fk_blog_post_tags_post
        FOREIGN KEY (post_id) REFERENCES blog_posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_blog_post_tags_tag
        FOREIGN KEY (tag_id) REFERENCES blog_tags(tag_id) ON DELETE CASCADE
);

-- Comments
COMMENT ON TABLE blog_post_tags IS 'Many-to-many relationship between blog posts and tags';

-- Indexes
CREATE INDEX idx_blog_post_tags_post_id ON blog_post_tags(post_id);
CREATE INDEX idx_blog_post_tags_tag_id ON blog_post_tags(tag_id);
