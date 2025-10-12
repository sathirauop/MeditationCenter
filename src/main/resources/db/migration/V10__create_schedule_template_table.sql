-- Create schedule_template table
CREATE TABLE schedule_template (
    template_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index
CREATE INDEX idx_schedule_template_active ON schedule_template(is_active);

-- Add comments
COMMENT ON TABLE schedule_template IS 'Reusable schedule templates (e.g., Daily Routine, Weekend Retreat Schedule)';
COMMENT ON COLUMN schedule_template.name IS 'Template name for identification';
