-- V17.1: Add trigger for blog_posts updated_at
-- Description: Adds trigger to auto-update updated_at timestamp on blog_posts table
-- Author: Sathira Basnayake
-- Date: 2025-12-18
-- Note: This file is NOT processed by jOOQ codegen (no "create" in filename)

-- Apply trigger to blog_posts table
-- Note: The update_updated_at_column() function already exists from previous migrations
CREATE TRIGGER update_blog_posts_updated_at
    BEFORE UPDATE ON blog_posts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
