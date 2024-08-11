CREATE TABLE IF NOT EXISTS POSTS_LIKES(
    author_id UUID NOT NULL,
    post_id UUID NOT NULL,
    CONSTRAINT posts_likesPK PRIMARY KEY (author_id, post_id),
    CONSTRAINT fk_posts_likes_author FOREIGN KEY (author_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_posts_likes_post FOREIGN KEY (post_id) REFERENCES POSTS(post_id)
);