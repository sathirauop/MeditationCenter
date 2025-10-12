-- Create payment table
-- Handles booking payments (multiple payments per booking allowed for installments, retries, etc.)
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

-- Create indexes
CREATE INDEX idx_payment_booking ON payment(booking_id);
CREATE INDEX idx_payment_user ON payment(user_id);
CREATE INDEX idx_payment_status ON payment(payment_status);
CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_payment_transaction ON payment(transaction_id);

-- Add comments
COMMENT ON TABLE payment IS 'Payment transactions for bookings (supports multiple payments per booking for installments, retries, refunds)';
COMMENT ON COLUMN payment.user_id IS 'User who made the payment';
COMMENT ON COLUMN payment.payment_status IS 'Status: PENDING, COMPLETED, FAILED, REFUNDED';
COMMENT ON COLUMN payment.payment_method IS 'Method: CASH, CARD, BANK_TRANSFER, ONLINE';
COMMENT ON COLUMN payment.transaction_id IS 'Unique transaction ID from payment gateway';
COMMENT ON COLUMN payment.payment_gateway_response IS 'Full response JSON from payment gateway for debugging';
COMMENT ON COLUMN payment.refund_amount IS 'Amount refunded (partial or full refund)';
