-- Create booking table
CREATE TABLE booking (
    booking_id BIGSERIAL PRIMARY KEY,
    meditation_program_id BIGINT NOT NULL,
    pricing_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    booking_type VARCHAR(50) NOT NULL,
    booking_date DATE NOT NULL,
    booking_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    participant_count INTEGER NOT NULL DEFAULT 1,
    amount DECIMAL(10,2) NOT NULL,
    special_requirements TEXT,
    cancellation_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_program FOREIGN KEY (meditation_program_id) REFERENCES meditation_program(meditation_program_id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_pricing FOREIGN KEY (pricing_id) REFERENCES pricing(pricing_id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_booking_amount_positive CHECK (amount >= 0),
    CONSTRAINT chk_participant_count_positive CHECK (participant_count > 0)
);

-- Create indexes
CREATE INDEX idx_booking_user ON booking(user_id);
CREATE INDEX idx_booking_program ON booking(meditation_program_id);
CREATE INDEX idx_booking_status ON booking(booking_status);
CREATE INDEX idx_booking_date ON booking(booking_date);

-- Add comments
COMMENT ON TABLE booking IS 'Meditation program bookings made by users';
COMMENT ON COLUMN booking.booking_type IS 'Type: DAILY, WEEKLY, MONTHLY, EVENT, RETREAT';
COMMENT ON COLUMN booking.booking_status IS 'Status: PENDING, CONFIRMED, CANCELLED, COMPLETED';
COMMENT ON COLUMN booking.participant_count IS 'Number of participants for this booking';
COMMENT ON COLUMN booking.special_requirements IS 'Any special needs or requirements for the booking';
