CREATE TABLE IF NOT EXISTS POSTS (
    post_id UUID PRIMARY KEY,
    author_id UUID NOT NULL,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    description TEXT,
    content BYTEA NOT NULL,
    are_hidden_likes BOOLEAN NOT NULL DEFAULT FALSE,
    are_disabled_comments BOOLEAN NOT NULL DEFAULT FALSE,
    likes_count INT NOT NULL DEFAULT 0 CHECK (likes_count > 0),
    comments_count INT NOT NULL DEFAULT 0 CHECK (comments_count > 0),
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES USERS(user_id)
);

CREATE INDEX IF NOT EXISTS ix_posts_author_id ON POSTS (author_id);
CREATE INDEX IF NOT EXISTS ix_posts_creation_datetime ON POSTS (creation_datetime);