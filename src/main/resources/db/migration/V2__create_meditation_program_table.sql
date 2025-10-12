-- Create meditation_program table
CREATE TABLE meditation_program (
    meditation_program_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    max_seats INTEGER NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_meditation_program_active ON meditation_program(is_active);

-- Add comments
COMMENT ON TABLE meditation_program IS 'Meditation programs offered by the center';
COMMENT ON COLUMN meditation_program.max_seats IS 'Maximum number of participants allowed';
