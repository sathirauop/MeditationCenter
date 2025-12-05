-- Create override_activity table
-- Stores all activities for a specific override date
-- When an override exists for a date, ALL activities for that day come from this table
-- (including unchanged activities copied from the template)
CREATE TABLE override_activity (
    id BIGSERIAL PRIMARY KEY,
    override_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes TEXT,
    is_cancelled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_override_activity_override FOREIGN KEY (override_id) REFERENCES schedule_override(override_id) ON DELETE CASCADE,
    CONSTRAINT fk_override_activity_activity FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    CONSTRAINT chk_override_time_valid CHECK (end_time > start_time)
);

-- Create indexes
CREATE INDEX idx_override_activity_override ON override_activity(override_id);
CREATE INDEX idx_override_activity_activity ON override_activity(activity_id);

-- Add comments
COMMENT ON TABLE override_activity IS 'Activities scheduled for a specific override date. When an override exists for a date, the entire day schedule comes from this table instead of the template';
COMMENT ON COLUMN override_activity.override_id IS 'References the schedule_override for a specific date';
COMMENT ON COLUMN override_activity.activity_id IS 'The activity being scheduled';
COMMENT ON COLUMN override_activity.start_time IS 'Start time for this activity on the override date';
COMMENT ON COLUMN override_activity.end_time IS 'End time for this activity on the override date';
COMMENT ON COLUMN override_activity.is_cancelled IS 'If true, this activity is cancelled for this specific date';
COMMENT ON COLUMN override_activity.notes IS 'Special notes or instructions for this activity on this override date';
