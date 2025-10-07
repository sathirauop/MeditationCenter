-- Create template_schedule_activity table
CREATE TABLE template_schedule_activity (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    day_of_week INTEGER,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_template_schedule_template FOREIGN KEY (template_id) REFERENCES schedule_template(template_id) ON DELETE CASCADE,
    CONSTRAINT fk_template_schedule_activity FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    CONSTRAINT chk_time_valid CHECK (end_time > start_time),
    CONSTRAINT chk_day_of_week CHECK (day_of_week IS NULL OR (day_of_week >= 0 AND day_of_week <= 6))
);

-- Create indexes
CREATE INDEX idx_template_schedule_activity_template ON template_schedule_activity(template_id);
CREATE INDEX idx_template_schedule_activity_activity ON template_schedule_activity(activity_id);
CREATE INDEX idx_template_schedule_activity_day ON template_schedule_activity(day_of_week);

-- Add comments
COMMENT ON TABLE template_schedule_activity IS 'Activities within a schedule template';
COMMENT ON COLUMN template_schedule_activity.day_of_week IS 'Day of week (0=Sunday, 6=Saturday, NULL=applies to all days)';
COMMENT ON COLUMN template_schedule_activity.notes IS 'Special instructions or notes for this scheduled activity';
