-- Create donation table
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

-- Create indexes
CREATE INDEX idx_donation_campaign ON donation(campaign_id);
CREATE INDEX idx_donation_user ON donation(user_id);
CREATE INDEX idx_donation_status ON donation(payment_status);
CREATE INDEX idx_donation_date ON donation(created_at);

-- Add comments
COMMENT ON TABLE donation IS 'Individual donation transactions';
COMMENT ON COLUMN donation.campaign_id IS 'Related campaign (NULL for general donations)';
COMMENT ON COLUMN donation.payment_status IS 'Status: PENDING, COMPLETED, FAILED, REFUNDED';
COMMENT ON COLUMN donation.payment_method IS 'Method: CASH, CARD, BANK_TRANSFER, ONLINE';
COMMENT ON COLUMN donation.is_anonymous IS 'Whether to hide donor name publicly';
COMMENT ON COLUMN donation.donor_message IS 'Optional message from donor';
