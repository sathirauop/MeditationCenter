-- Create schedule_override table
CREATE TABLE schedule_override (
    override_id BIGSERIAL PRIMARY KEY,
    override_date DATE NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index
CREATE INDEX idx_schedule_override_date ON schedule_override(override_date);

-- Add comments
COMMENT ON TABLE schedule_override IS 'Overrides for specific dates when the regular template schedule should not apply';
COMMENT ON COLUMN schedule_override.override_date IS 'The date for which this override applies (unique - one override per date)';