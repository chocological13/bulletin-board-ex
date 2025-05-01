-- Schema for PostgreSQL testing
DROP TABLE IF EXISTS posts;

CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL CHECK (LENGTH(title) <= 100),
    author VARCHAR(20) NOT NULL CHECK (LENGTH(author) <= 20),
    password VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    view_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Clean up existing data
TRUNCATE TABLE posts;

-- Test posts
INSERT INTO posts (id, title, author, password, content, view_count, created_at)
VALUES
    (1, 'Test Title 1', 'Test Author 1', 'hashedpassword', 'Test content for post 1', 5, CURRENT_TIMESTAMP),
    (2, 'Test Title 2', 'Test Author 2', 'hashedpassword', 'Test content for post 2', 10, CURRENT_TIMESTAMP),
    (3, 'Deleted Post', 'Deleted Author', 'hashedpassword', 'This post is deleted', 3, CURRENT_TIMESTAMP);

-- Mark 3 as deleted
UPDATE posts SET deleted_at = CURRENT_TIMESTAMP WHERE id = 3;

-- Reset the sequence
SELECT setval('posts_id_seq', (SELECT MAX(id) FROM posts));