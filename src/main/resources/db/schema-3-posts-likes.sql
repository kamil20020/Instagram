CREATE TABLE IF NOT EXISTS POSTS_LIKES(
    post_like_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    CONSTRAINT fk_posts_likes_user FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_posts_likes_post FOREIGN KEY (post_id) REFERENCES POSTS(post_id)
);