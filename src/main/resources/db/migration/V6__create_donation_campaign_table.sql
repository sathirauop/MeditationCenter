-- Create donation_campaign table
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

-- Create indexes
CREATE INDEX idx_donation_campaign_active ON donation_campaign(is_active);
CREATE INDEX idx_donation_campaign_dates ON donation_campaign(start_date, end_date);

-- Add comments
COMMENT ON TABLE donation_campaign IS 'Donation campaigns and fundraising initiatives';
COMMENT ON COLUMN donation_campaign.target_amount IS 'Fundraising goal (NULL for general donations)';
COMMENT ON COLUMN donation_campaign.current_amount IS 'Total amount raised so far';
COMMENT ON COLUMN donation_campaign.start_date IS 'Campaign start date (NULL for ongoing)';
COMMENT ON COLUMN donation_campaign.end_date IS 'Campaign end date (NULL for indefinite)';
