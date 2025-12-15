-- Create books table for PDF resource management
CREATE TABLE books (
    book_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    description TEXT,
    pdf_file_key VARCHAR(255) NOT NULL,
    cover_image_key VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_pdf_file_key_not_empty CHECK (pdf_file_key <> '')
);

-- Create indexes for commonly queried columns
CREATE INDEX idx_books_active ON books(is_active);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_created_at ON books(created_at DESC);

-- Add table and column comments for documentation
COMMENT ON TABLE books IS 'Digital books and PDF resources available for download';
COMMENT ON COLUMN books.book_id IS 'Primary key for book identification';
COMMENT ON COLUMN books.title IS 'Book title';
COMMENT ON COLUMN books.author IS 'Book author name (optional)';
COMMENT ON COLUMN books.description IS 'Book description or summary';
COMMENT ON COLUMN books.pdf_file_key IS 'Cloudflare R2 storage key for the PDF file';
COMMENT ON COLUMN books.cover_image_key IS 'Cloudflare R2 storage key for the cover image (optional)';
COMMENT ON COLUMN books.is_active IS 'Whether the book is active and available for download';
COMMENT ON COLUMN books.created_at IS 'Timestamp when the book was created';
COMMENT ON COLUMN books.updated_at IS 'Timestamp when the book was last updated';
