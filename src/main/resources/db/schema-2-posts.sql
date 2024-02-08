CREATE TABLE IF NOT EXISTS POSTS (
    post_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    description TEXT NOT NULL,
    img BYTEA NOT NULL,
    are_hidden_likes BOOLEAN NOT NULL DEFAULT FALSE,
    are_disabled_comments BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);