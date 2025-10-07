-- Create event_registration table
CREATE TABLE event_registration (
    registration_id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    registration_status VARCHAR(50) NOT NULL DEFAULT 'REGISTERED',
    checked_in_at TIMESTAMP,
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancellation_date TIMESTAMP,
    cancellation_reason TEXT,
    CONSTRAINT fk_event_registration_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_event_registration_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_event_user UNIQUE (event_id, user_id)
);

-- Create indexes
CREATE INDEX idx_event_registration_event ON event_registration(event_id);
CREATE INDEX idx_event_registration_user ON event_registration(user_id);
CREATE INDEX idx_event_registration_status ON event_registration(registration_status);

-- Add comments
COMMENT ON TABLE event_registration IS 'User registrations for events';
COMMENT ON COLUMN event_registration.registration_status IS 'Status: REGISTERED, ATTENDED, CANCELLED, NO_SHOW';
COMMENT ON COLUMN event_registration.checked_in_at IS 'Timestamp when user checked in at the event';
COMMENT ON CONSTRAINT uk_event_user ON event_registration IS 'Prevent duplicate registrations for same event';
