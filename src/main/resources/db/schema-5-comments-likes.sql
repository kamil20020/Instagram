CREATE TABLE IF NOT EXISTS COMMENTS_LIKES (
    comment_like_id UUID PRIMARY KEY,
    comment_id UUID NOT NULL,
    author_id UUID NOT NULL,
    CONSTRAINT fk_comments_likes_comment FOREIGN KEY (comment_id) REFERENCES COMMENTS(comment_id),
    CONSTRAINT fk_comments_likes_author FOREIGN KEY (author_id) REFERENCES USERS(user_id)
);