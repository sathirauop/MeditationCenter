-- Create pricing table
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

-- Create indexes
CREATE INDEX idx_pricing_booking_type ON pricing(booking_type);
CREATE INDEX idx_pricing_active ON pricing(is_active);
CREATE INDEX idx_pricing_dates ON pricing(effective_from_date, effective_to_date);

-- Add comments
COMMENT ON TABLE pricing IS 'Pricing rules for different booking types with date-based validity';
COMMENT ON COLUMN pricing.booking_type IS 'Type of booking: DAILY, WEEKLY, MONTHLY, EVENT, RETREAT';
COMMENT ON COLUMN pricing.effective_from_date IS 'Start date when this pricing becomes effective';
COMMENT ON COLUMN pricing.effective_to_date IS 'End date when this pricing expires (NULL means indefinite)';
