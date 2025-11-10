-- Create events table
CREATE TABLE events (
    event_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(255),
    cover_image_key VARCHAR(255),
    gallery_image_keys TEXT[],
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_event_time_valid CHECK (end_time > start_time)
);

-- Create indexes
CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_events_active ON events(is_active);

-- Add comments
COMMENT ON TABLE events IS 'Special events, ceremonies, and workshops';
COMMENT ON COLUMN events.name IS 'Event name/title';
COMMENT ON COLUMN events.event_date IS 'Date when the event occurs';
COMMENT ON COLUMN events.start_time IS 'Start time of the event';
COMMENT ON COLUMN events.end_time IS 'End time of the event';
COMMENT ON COLUMN events.location IS 'Event location/venue';
COMMENT ON COLUMN events.cover_image_key IS 'S3/R2 key for cover image';
COMMENT ON COLUMN events.gallery_image_keys IS 'Array of S3/R2 keys for gallery images';
