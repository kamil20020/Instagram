CREATE TABLE IF NOT EXISTS POSTS_LIKES(
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL,
    post_id UUID NOT NULL,
    CONSTRAINT fk_posts_likes_author FOREIGN KEY (author_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_posts_likes_post FOREIGN KEY (post_id) REFERENCES POSTS(post_id)
);