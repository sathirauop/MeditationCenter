-- Add Sinhala language support for events and meditation programs
-- This migration adds name_si (Sinhala name) and description_si (Sinhala description) fields

-- Add Sinhala fields to events table
ALTER TABLE events
    ADD COLUMN name_si VARCHAR(255),
    ADD COLUMN description_si TEXT;

-- Add Sinhala fields to meditation_program table
ALTER TABLE meditation_program
    ADD COLUMN name_si VARCHAR(255),
    ADD COLUMN description_si TEXT;

-- Add comments for new columns
COMMENT ON COLUMN events.name_si IS 'Event name in Sinhala language';
COMMENT ON COLUMN events.description_si IS 'Event description in Sinhala language';
COMMENT ON COLUMN meditation_program.name_si IS 'Program name in Sinhala language';
COMMENT ON COLUMN meditation_program.description_si IS 'Program description in Sinhala language';
