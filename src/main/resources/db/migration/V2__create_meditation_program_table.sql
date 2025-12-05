-- Create meditation_program table
CREATE TABLE meditation_program (
    meditation_program_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    max_seats INTEGER NOT NULL DEFAULT 0,
    cover_image_key VARCHAR(255),
    gallery_image_keys TEXT[],
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_meditation_program_active ON meditation_program(is_active);

-- Add comments
COMMENT ON TABLE meditation_program IS 'Meditation programs offered by the center';
COMMENT ON COLUMN meditation_program.max_seats IS 'Maximum number of participants allowed';
COMMENT ON COLUMN meditation_program.cover_image_key IS 'S3/R2 key for cover image';
COMMENT ON COLUMN meditation_program.gallery_image_keys IS 'Array of S3/R2 keys for gallery images';
