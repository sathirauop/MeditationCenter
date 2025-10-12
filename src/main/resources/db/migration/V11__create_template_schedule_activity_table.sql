-- Create template_schedule_activity table
-- Links activities to templates with specific start/end times
CREATE TABLE template_schedule_activity (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_template_schedule_template FOREIGN KEY (template_id) REFERENCES schedule_template(template_id) ON DELETE CASCADE,
    CONSTRAINT fk_template_schedule_activity FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    CONSTRAINT chk_time_valid CHECK (end_time > start_time)
);

-- Create indexes
CREATE INDEX idx_template_schedule_activity_template ON template_schedule_activity(template_id);
CREATE INDEX idx_template_schedule_activity_activity ON template_schedule_activity(activity_id);

-- Add comments
COMMENT ON TABLE template_schedule_activity IS 'Activities within a schedule template with scheduled times';
COMMENT ON COLUMN template_schedule_activity.start_time IS 'Start time for this activity';
COMMENT ON COLUMN template_schedule_activity.end_time IS 'End time for this activity';
COMMENT ON COLUMN template_schedule_activity.notes IS 'Special instructions or notes for this scheduled activity';
