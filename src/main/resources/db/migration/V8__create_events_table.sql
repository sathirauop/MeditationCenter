-- Create events table
CREATE TABLE events (
    event_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(255),
    max_participants INTEGER,
    current_participants INTEGER NOT NULL DEFAULT 0,
    images TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    requires_registration BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_event_time_valid CHECK (end_time > start_time),
    CONSTRAINT chk_max_participants_positive CHECK (max_participants IS NULL OR max_participants > 0),
    CONSTRAINT chk_current_participants_valid CHECK (current_participants >= 0 AND (max_participants IS NULL OR current_participants <= max_participants))
);

-- Create indexes
CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_events_active ON events(is_active);
CREATE INDEX idx_events_registration ON events(requires_registration);

-- Add comments
COMMENT ON TABLE events IS 'Special events, ceremonies, and workshops';
COMMENT ON COLUMN events.images IS 'JSON array or comma-separated URLs of event images';
COMMENT ON COLUMN events.requires_registration IS 'Whether users need to register for this event';
COMMENT ON COLUMN events.current_participants IS 'Current number of registered participants';
