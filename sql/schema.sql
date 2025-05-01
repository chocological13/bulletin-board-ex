-- This file is to initiate the table
DROP TABLE IF EXISTS posts;

CREATE TABLE posts (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(100) NOT NULL CHECK (LENGTH(title) <= 100),
                       author VARCHAR(20) NOT NULL CHECK (LENGTH(author) <= 20),
                       password VARCHAR(100) NOT NULL,
                       content TEXT NOT NULL,
                       view_count INTEGER DEFAULT 0,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       modified_at TIMESTAMP,
                       deleted_at TIMESTAMP
);

-- Sample data
INSERT INTO posts (title, author, password, content)
VALUES
    ('Welcome to the Bulletin Board', 'Admin', '$2a$10$xPeeHUHSNqEtzrY70c5V5.PnKCpkywTXkT8TeTHJSEtK2n.A9.4cC', 'This is the first post on our bulletin board system. Feel free to share your thoughts!'),
    ('How to use this bulletin board', 'Admin', '$2a$10$xPeeHUHSNqEtzrY70c5V5.PnKCpkywTXkT8TeTHJSEtK2n.A9.4cC', 'This bulletin board allows you to create, edit, and delete posts. To edit or delete a post, you need to enter the password you used when creating it.');