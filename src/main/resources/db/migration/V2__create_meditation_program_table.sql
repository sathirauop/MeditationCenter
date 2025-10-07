-- Create meditation_program table
CREATE TABLE meditation_program (
    meditation_program_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    max_seats INTEGER NOT NULL DEFAULT 0,
    duration_minutes INTEGER,
    instructor_id BIGINT,
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_program_instructor FOREIGN KEY (instructor_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Create indexes
CREATE INDEX idx_meditation_program_active ON meditation_program(is_active);
CREATE INDEX idx_meditation_program_instructor ON meditation_program(instructor_id);

-- Add comments
COMMENT ON TABLE meditation_program IS 'Meditation programs offered by the center';
COMMENT ON COLUMN meditation_program.max_seats IS 'Maximum number of participants allowed';
COMMENT ON COLUMN meditation_program.duration_minutes IS 'Typical duration of the program in minutes';
COMMENT ON COLUMN meditation_program.instructor_id IS 'Primary instructor for this program';
