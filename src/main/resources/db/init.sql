-- Consolidated Flyway migrations for jOOQ code generation
-- This file combines all V1-V13 migrations in order

-- V1: Create users table
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

COMMENT ON TABLE users IS 'User accounts for meditation center management system';
COMMENT ON COLUMN users.role IS 'User role: USER, ADMIN, INSTRUCTOR';
COMMENT ON COLUMN users.mobile_number IS 'Mobile number in international format (e.g., +94771234567)';

-- V2: Create meditation_program table
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

CREATE INDEX idx_meditation_program_active ON meditation_program(is_active);
CREATE INDEX idx_meditation_program_instructor ON meditation_program(instructor_id);

COMMENT ON TABLE meditation_program IS 'Meditation programs offered by the center';
COMMENT ON COLUMN meditation_program.max_seats IS 'Maximum number of participants allowed';
COMMENT ON COLUMN meditation_program.duration_minutes IS 'Typical duration of the program in minutes';
COMMENT ON COLUMN meditation_program.instructor_id IS 'Primary instructor for this program';

-- V3: Create pricing table
CREATE TABLE pricing (
    pricing_id BIGSERIAL PRIMARY KEY,
    booking_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    effective_from_date DATE NOT NULL,
    effective_to_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_price_positive CHECK (price >= 0),
    CONSTRAINT chk_date_range CHECK (effective_to_date IS NULL OR effective_to_date >= effective_from_date)
);

CREATE INDEX idx_pricing_booking_type ON pricing(booking_type);
CREATE INDEX idx_pricing_active ON pricing(is_active);
CREATE INDEX idx_pricing_dates ON pricing(effective_from_date, effective_to_date);

COMMENT ON TABLE pricing IS 'Pricing rules for different booking types with date-based validity';
COMMENT ON COLUMN pricing.booking_type IS 'Type of booking: DAILY, WEEKLY, MONTHLY, EVENT, RETREAT';
COMMENT ON COLUMN pricing.effective_from_date IS 'Start date when this pricing becomes effective';
COMMENT ON COLUMN pricing.effective_to_date IS 'End date when this pricing expires (NULL means indefinite)';

-- V4: Create booking table
CREATE TABLE booking (
    booking_id BIGSERIAL PRIMARY KEY,
    meditation_program_id BIGINT NOT NULL,
    pricing_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    booking_type VARCHAR(50) NOT NULL,
    booking_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
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

CREATE INDEX idx_booking_user ON booking(user_id);
CREATE INDEX idx_booking_program ON booking(meditation_program_id);
CREATE INDEX idx_booking_status ON booking(status);
CREATE INDEX idx_booking_date ON booking(booking_date);

COMMENT ON TABLE booking IS 'Meditation program bookings made by users';
COMMENT ON COLUMN booking.booking_type IS 'Type: DAILY, WEEKLY, MONTHLY, EVENT, RETREAT';
COMMENT ON COLUMN booking.status IS 'Status: PENDING, CONFIRMED, CANCELLED, COMPLETED';
COMMENT ON COLUMN booking.participant_count IS 'Number of participants for this booking';
COMMENT ON COLUMN booking.special_requirements IS 'Any special needs or requirements for the booking';

-- V5: Create payment table
CREATE TABLE payment (
    payment_id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    payment_date TIMESTAMP,
    payment_gateway_response TEXT,
    refund_amount DECIMAL(10,2) DEFAULT 0.00,
    refund_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_amount_positive CHECK (amount >= 0),
    CONSTRAINT chk_refund_amount_valid CHECK (refund_amount >= 0 AND refund_amount <= amount)
);

CREATE INDEX idx_payment_booking ON payment(booking_id);
CREATE INDEX idx_payment_status ON payment(status);
CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_payment_transaction ON payment(transaction_id);

COMMENT ON TABLE payment IS 'Payment transactions for bookings';
COMMENT ON COLUMN payment.status IS 'Status: PENDING, COMPLETED, FAILED, REFUNDED';
COMMENT ON COLUMN payment.payment_method IS 'Method: CASH, CARD, BANK_TRANSFER, ONLINE';
COMMENT ON COLUMN payment.transaction_id IS 'Unique transaction ID from payment gateway';
COMMENT ON COLUMN payment.payment_gateway_response IS 'Full response JSON from payment gateway for debugging';

-- V6: Create donation_campaign table
CREATE TABLE donation_campaign (
    campaign_id BIGSERIAL PRIMARY KEY,
    campaign_name VARCHAR(255) NOT NULL,
    description TEXT,
    target_amount DECIMAL(10,2),
    current_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_campaign_target_positive CHECK (target_amount IS NULL OR target_amount > 0),
    CONSTRAINT chk_campaign_current_positive CHECK (current_amount >= 0),
    CONSTRAINT chk_campaign_date_range CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

CREATE INDEX idx_donation_campaign_active ON donation_campaign(is_active);
CREATE INDEX idx_donation_campaign_dates ON donation_campaign(start_date, end_date);

COMMENT ON TABLE donation_campaign IS 'Donation campaigns and fundraising initiatives';
COMMENT ON COLUMN donation_campaign.target_amount IS 'Fundraising goal (NULL for general donations)';
COMMENT ON COLUMN donation_campaign.current_amount IS 'Total amount raised so far';
COMMENT ON COLUMN donation_campaign.start_date IS 'Campaign start date (NULL for ongoing)';
COMMENT ON COLUMN donation_campaign.end_date IS 'Campaign end date (NULL for indefinite)';

-- V7: Create donation table
CREATE TABLE donation (
    donation_id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT,
    user_id BIGINT NOT NULL,
    donation_amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_gateway_response TEXT,
    is_anonymous BOOLEAN NOT NULL DEFAULT false,
    donor_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donation_campaign FOREIGN KEY (campaign_id) REFERENCES donation_campaign(campaign_id) ON DELETE SET NULL,
    CONSTRAINT fk_donation_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_donation_amount_positive CHECK (donation_amount > 0)
);

CREATE INDEX idx_donation_campaign ON donation(campaign_id);
CREATE INDEX idx_donation_user ON donation(user_id);
CREATE INDEX idx_donation_status ON donation(payment_status);
CREATE INDEX idx_donation_date ON donation(created_at);

COMMENT ON TABLE donation IS 'Individual donation transactions';
COMMENT ON COLUMN donation.campaign_id IS 'Related campaign (NULL for general donations)';
COMMENT ON COLUMN donation.payment_status IS 'Status: PENDING, COMPLETED, FAILED, REFUNDED';
COMMENT ON COLUMN donation.payment_method IS 'Method: CASH, CARD, BANK_TRANSFER, ONLINE';
COMMENT ON COLUMN donation.is_anonymous IS 'Whether to hide donor name publicly';
COMMENT ON COLUMN donation.donor_message IS 'Optional message from donor';

-- V8: Create events table
CREATE TABLE events (
    event_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(255),
    max_participants INTEGER,
    current_participants INTEGER NOT NULL DEFAULT 0,
    images TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    requires_registration BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_event_time_valid CHECK (end_time > start_time),
    CONSTRAINT chk_max_participants_positive CHECK (max_participants IS NULL OR max_participants > 0),
    CONSTRAINT chk_current_participants_valid CHECK (current_participants >= 0 AND (max_participants IS NULL OR current_participants <= max_participants))
);

CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_events_active ON events(is_active);
CREATE INDEX idx_events_registration ON events(requires_registration);

COMMENT ON TABLE events IS 'Special events, ceremonies, and workshops';
COMMENT ON COLUMN events.images IS 'JSON array or comma-separated URLs of event images';
COMMENT ON COLUMN events.requires_registration IS 'Whether users need to register for this event';
COMMENT ON COLUMN events.current_participants IS 'Current number of registered participants';

-- V9: Create event_registration table
CREATE TABLE event_registration (
    registration_id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'REGISTERED',
    checked_in_at TIMESTAMP,
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancellation_date TIMESTAMP,
    cancellation_reason TEXT,
    CONSTRAINT fk_event_registration_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_event_registration_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_event_user UNIQUE (event_id, user_id)
);

CREATE INDEX idx_event_registration_event ON event_registration(event_id);
CREATE INDEX idx_event_registration_user ON event_registration(user_id);
CREATE INDEX idx_event_registration_status ON event_registration(status);

COMMENT ON TABLE event_registration IS 'User registrations for events';
COMMENT ON COLUMN event_registration.status IS 'Status: REGISTERED, ATTENDED, CANCELLED, NO_SHOW';
COMMENT ON COLUMN event_registration.checked_in_at IS 'Timestamp when user checked in at the event';
COMMENT ON CONSTRAINT uk_event_user ON event_registration IS 'Prevent duplicate registrations for same event';

-- V10: Create activity table
CREATE TABLE activity (
    activity_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activity_title ON activity(title);

COMMENT ON TABLE activity IS 'Reusable activity definitions for schedules';
COMMENT ON COLUMN activity.title IS 'Activity name (e.g., Morning Meditation, Dharma Talk, Walking Meditation)';
COMMENT ON COLUMN activity.location IS 'Default location where this activity takes place';

-- V11: Create schedule_template table
CREATE TABLE schedule_template (
    template_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_schedule_template_active ON schedule_template(is_active);

COMMENT ON TABLE schedule_template IS 'Reusable schedule templates (e.g., Daily Routine, Weekend Retreat Schedule)';
COMMENT ON COLUMN schedule_template.name IS 'Template name for identification';

-- V12: Create template_schedule_activity table
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

CREATE INDEX idx_template_schedule_activity_template ON template_schedule_activity(template_id);
CREATE INDEX idx_template_schedule_activity_activity ON template_schedule_activity(activity_id);
CREATE INDEX idx_template_schedule_activity_day ON template_schedule_activity(day_of_week);

COMMENT ON TABLE template_schedule_activity IS 'Activities within a schedule template';
COMMENT ON COLUMN template_schedule_activity.day_of_week IS 'Day of week (0=Sunday, 6=Saturday, NULL=applies to all days)';
COMMENT ON COLUMN template_schedule_activity.notes IS 'Special instructions or notes for this scheduled activity';

-- V13: Create schedule_override table
CREATE TABLE schedule_override (
    override_id BIGSERIAL PRIMARY KEY,
    template_schedule_activity_id BIGINT NOT NULL,
    override_date DATE NOT NULL,
    new_start_time TIME,
    new_end_time TIME,
    is_cancelled BOOLEAN NOT NULL DEFAULT false,
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_override_template_activity FOREIGN KEY (template_schedule_activity_id) REFERENCES template_schedule_activity(id) ON DELETE CASCADE,
    CONSTRAINT chk_override_time_valid CHECK (is_cancelled = true OR (new_start_time IS NOT NULL AND new_end_time IS NOT NULL AND new_end_time > new_start_time)),
    CONSTRAINT uk_override_activity_date UNIQUE (template_schedule_activity_id, override_date)
);

CREATE INDEX idx_schedule_override_activity ON schedule_override(template_schedule_activity_id);
CREATE INDEX idx_schedule_override_date ON schedule_override(override_date);
CREATE INDEX idx_schedule_override_cancelled ON schedule_override(is_cancelled);

COMMENT ON TABLE schedule_override IS 'Date-specific overrides for scheduled activities';
COMMENT ON COLUMN schedule_override.override_date IS 'Specific date this override applies to';
COMMENT ON COLUMN schedule_override.new_start_time IS 'Override start time (NULL if cancelled)';
COMMENT ON COLUMN schedule_override.new_end_time IS 'Override end time (NULL if cancelled)';
COMMENT ON COLUMN schedule_override.is_cancelled IS 'Whether this activity is cancelled on this date';
COMMENT ON COLUMN schedule_override.reason IS 'Reason for time change or cancellation';
COMMENT ON CONSTRAINT uk_override_activity_date ON schedule_override IS 'One override per activity per date';
