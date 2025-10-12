-- Create users table
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

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on role for authorization queries
CREATE INDEX idx_users_role ON users(role);

-- Add comment to table
COMMENT ON TABLE users IS 'User accounts for meditation center management system';
COMMENT ON COLUMN users.role IS 'User role: USER, ADMIN';
COMMENT ON COLUMN users.mobile_number IS 'Mobile number in international format (e.g., +94771234567)';
