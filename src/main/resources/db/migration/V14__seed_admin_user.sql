-- Seed initial admin user
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

-- Add comment
COMMENT ON TABLE users IS 'Default admin credentials: admin@meditationcenter.com / admin123 (CHANGE IN PRODUCTION!)';
