-- Create schedule_override table
CREATE TABLE schedule_override (
    override_id BIGSERIAL PRIMARY KEY,
    template_schedule_activity_id BIGINT NOT NULL,
    override_date DATE NOT NULL,
    new_start_time TIME,
    new_end_time TIME,
    is_cancelled BOOLEAN NOT NULL DEFAULT false,
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_override_template_activity FOREIGN KEY (template_schedule_activity_id) REFERENCES template_schedule_activity(id) ON DELETE CASCADE,
    CONSTRAINT chk_override_time_valid CHECK (is_cancelled = true OR (new_start_time IS NOT NULL AND new_end_time IS NOT NULL AND new_end_time > new_start_time)),
    CONSTRAINT uk_override_activity_date UNIQUE (template_schedule_activity_id, override_date)
);

-- Create indexes
CREATE INDEX idx_schedule_override_activity ON schedule_override(template_schedule_activity_id);
CREATE INDEX idx_schedule_override_date ON schedule_override(override_date);
CREATE INDEX idx_schedule_override_cancelled ON schedule_override(is_cancelled);

-- Add comments
COMMENT ON TABLE schedule_override IS 'Date-specific overrides for scheduled activities';
COMMENT ON COLUMN schedule_override.override_date IS 'Specific date this override applies to';
COMMENT ON COLUMN schedule_override.new_start_time IS 'Override start time (NULL if cancelled)';
COMMENT ON COLUMN schedule_override.new_end_time IS 'Override end time (NULL if cancelled)';
COMMENT ON COLUMN schedule_override.is_cancelled IS 'Whether this activity is cancelled on this date';
COMMENT ON COLUMN schedule_override.reason IS 'Reason for time change or cancellation';
COMMENT ON CONSTRAINT uk_override_activity_date ON schedule_override IS 'One override per activity per date';
