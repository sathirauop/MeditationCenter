-- ============================================================================
-- Meditation Center Database Schema - Complete Initialization Script
-- Author: Sathira Basnayake
-- Description: Complete database schema with all tables, indexes, and constraints
-- Version: 1.0
-- Date: 2025-10-12
-- ============================================================================

-- Drop existing database if exists and create fresh
DROP DATABASE IF EXISTS meditation_db;
CREATE DATABASE meditation_db;

-- Connect to the database
\c meditation_db;

-- ============================================================================
-- V1: Users Table
-- ============================================================================

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
COMMENT ON COLUMN users.role IS 'User role: USER, ADMIN';
COMMENT ON COLUMN users.mobile_number IS 'Mobile number in international format (e.g., +94771234567)';

-- ============================================================================
-- V2: Meditation Program Table
-- ============================================================================

CREATE TABLE meditation_program (
    meditation_program_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    max_seats INTEGER NOT NULL DEFAULT 0,
    duration_minutes INTEGER,
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_meditation_program_active ON meditation_program(is_active);

COMMENT ON TABLE meditation_program IS 'Meditation programs offered by the center';
COMMENT ON COLUMN meditation_program.max_seats IS 'Maximum number of participants allowed';
COMMENT ON COLUMN meditation_program.duration_minutes IS 'Typical duration of the program in minutes';

-- ============================================================================
-- V3: Pricing Table
-- ============================================================================

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

-- ============================================================================
-- V4: Booking Table
-- ============================================================================

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

CREATE INDEX idx_booking_user ON booking(user_id);
CREATE INDEX idx_booking_program ON booking(meditation_program_id);
CREATE INDEX idx_booking_status ON booking(booking_status);
CREATE INDEX idx_booking_date ON booking(booking_date);

COMMENT ON TABLE booking IS 'Meditation program bookings made by users';
COMMENT ON COLUMN booking.booking_type IS 'Type: DAILY, WEEKLY, MONTHLY, EVENT, RETREAT';
COMMENT ON COLUMN booking.booking_status IS 'Status: PENDING, CONFIRMED, CANCELLED, COMPLETED';
COMMENT ON COLUMN booking.participant_count IS 'Number of participants for this booking';
COMMENT ON COLUMN booking.special_requirements IS 'Any special needs or requirements for the booking';

-- ============================================================================
-- V5: Payment Table (Booking Payments)
-- ============================================================================

CREATE TABLE payment (
    payment_id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    payment_date TIMESTAMP,
    payment_gateway_response TEXT,
    refund_amount DECIMAL(10,2) DEFAULT 0.00,
    refund_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_amount_positive CHECK (amount >= 0),
    CONSTRAINT chk_refund_amount_valid CHECK (refund_amount >= 0 AND refund_amount <= amount)
);

CREATE INDEX idx_payment_booking ON payment(booking_id);
CREATE INDEX idx_payment_user ON payment(user_id);
CREATE INDEX idx_payment_status ON payment(payment_status);
CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_payment_transaction ON payment(transaction_id);

COMMENT ON TABLE payment IS 'Payment transactions for bookings (supports multiple payments per booking for installments, retries, refunds)';
COMMENT ON COLUMN payment.user_id IS 'User who made the payment';
COMMENT ON COLUMN payment.payment_status IS 'Status: PENDING, COMPLETED, FAILED, REFUNDED';
COMMENT ON COLUMN payment.payment_method IS 'Method: CASH, CARD, BANK_TRANSFER, ONLINE';
COMMENT ON COLUMN payment.transaction_id IS 'Unique transaction ID from payment gateway';
COMMENT ON COLUMN payment.payment_gateway_response IS 'Full response JSON from payment gateway for debugging';
COMMENT ON COLUMN payment.refund_amount IS 'Amount refunded (partial or full refund)';

-- ============================================================================
-- V6: Donation Campaign Table
-- ============================================================================

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

-- ============================================================================
-- V7: Donation Table (Donation Payments)
-- ============================================================================

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

-- ============================================================================
-- V8: Events Table
-- ============================================================================

CREATE TABLE events (
    event_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(255),
    images TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_event_time_valid CHECK (end_time > start_time)
);

CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_events_active ON events(is_active);

COMMENT ON TABLE events IS 'Special events, ceremonies, and workshops';
COMMENT ON COLUMN events.name IS 'Event name/title';
COMMENT ON COLUMN events.event_date IS 'Date when the event occurs';
COMMENT ON COLUMN events.start_time IS 'Start time of the event';
COMMENT ON COLUMN events.end_time IS 'End time of the event';
COMMENT ON COLUMN events.location IS 'Event location/venue';
COMMENT ON COLUMN events.images IS 'JSON array or comma-separated URLs of event images';

-- ============================================================================
-- V9: Activity Table
-- ============================================================================

CREATE TABLE activity (
    activity_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activity_title ON activity(title);

COMMENT ON TABLE activity IS 'Reusable activity definitions for schedules';
COMMENT ON COLUMN activity.title IS 'Activity name (e.g., Morning Meditation, Dharma Talk, Walking Meditation)';

-- ============================================================================
-- V10: Schedule Template Table
-- ============================================================================

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

-- ============================================================================
-- V11: Template Schedule Activity Table
-- ============================================================================

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

CREATE INDEX idx_template_schedule_activity_template ON template_schedule_activity(template_id);
CREATE INDEX idx_template_schedule_activity_activity ON template_schedule_activity(activity_id);

COMMENT ON TABLE template_schedule_activity IS 'Activities within a schedule template with scheduled times';
COMMENT ON COLUMN template_schedule_activity.start_time IS 'Start time for this activity';
COMMENT ON COLUMN template_schedule_activity.end_time IS 'End time for this activity';
COMMENT ON COLUMN template_schedule_activity.notes IS 'Special instructions or notes for this scheduled activity';

-- ============================================================================
-- V12: Schedule Override Table
-- ============================================================================

CREATE TABLE schedule_override (
    override_id BIGSERIAL PRIMARY KEY,
    override_date DATE NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_schedule_override_date ON schedule_override(override_date);

COMMENT ON TABLE schedule_override IS 'Overrides for specific dates when the regular template schedule should not apply';
COMMENT ON COLUMN schedule_override.override_date IS 'The date for which this override applies (unique - one override per date)';

-- ============================================================================
-- V13: Override Activity Table
-- ============================================================================

CREATE TABLE override_activity (
    id BIGSERIAL PRIMARY KEY,
    override_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes TEXT,
    is_cancelled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_override_activity_override FOREIGN KEY (override_id) REFERENCES schedule_override(override_id) ON DELETE CASCADE,
    CONSTRAINT fk_override_activity_activity FOREIGN KEY (activity_id) REFERENCES activity(activity_id) ON DELETE CASCADE,
    CONSTRAINT chk_override_time_valid CHECK (end_time > start_time)
);

CREATE INDEX idx_override_activity_override ON override_activity(override_id);
CREATE INDEX idx_override_activity_activity ON override_activity(activity_id);

COMMENT ON TABLE override_activity IS 'Activities scheduled for a specific override date. When an override exists for a date, the entire day schedule comes from this table instead of the template';
COMMENT ON COLUMN override_activity.override_id IS 'References the schedule_override for a specific date';
COMMENT ON COLUMN override_activity.activity_id IS 'The activity being scheduled';
COMMENT ON COLUMN override_activity.start_time IS 'Start time for this activity on the override date';
COMMENT ON COLUMN override_activity.end_time IS 'End time for this activity on the override date';
COMMENT ON COLUMN override_activity.is_cancelled IS 'If true, this activity is cancelled for this specific date';
COMMENT ON COLUMN override_activity.notes IS 'Special notes or instructions for this activity on this override date';

-- ============================================================================
-- V14: Seed Admin User
-- ============================================================================

-- Password: admin123 (BCrypt hash with 10 rounds)
-- IMPORTANT: Change this password immediately after first login in production!

INSERT INTO users (
    email,
    password,
    name,
    mobile_number,
    role,
    is_active,
    email_verified
) VALUES (
    'admin@meditationcenter.com',
    '$2a$10$2xwiXBeaYO2TjTO6BvTwluGENiBcK7PkZgyLZGmbOBh9.WqfhE5eG',  -- BCrypt hash of "admin123"
    'System Administrator',
    '+94771234567',
    'ADMIN',
    true,
    true
)
ON CONFLICT (email) DO NOTHING;  -- Prevent duplicate if already exists

COMMENT ON TABLE users IS 'Default admin credentials: admin@meditationcenter.com / admin123 (CHANGE IN PRODUCTION!)';

-- ============================================================================
-- End of Initialization Script
-- ============================================================================

-- Display summary
SELECT 'Database initialization complete!' AS status;
SELECT 'Total tables created: 13' AS info;
SELECT 'Admin user: admin@meditationcenter.com / admin123' AS credentials;
SELECT 'WARNING: Change admin password in production!' AS warning;
