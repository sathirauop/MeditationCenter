-- Create activity table
CREATE TABLE activity (
    activity_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index
CREATE INDEX idx_activity_title ON activity(title);

-- Add comments
COMMENT ON TABLE activity IS 'Reusable activity definitions for schedules';
COMMENT ON COLUMN activity.title IS 'Activity name (e.g., Morning Meditation, Dharma Talk, Walking Meditation)';
