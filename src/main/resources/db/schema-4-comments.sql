CREATE TABLE IF NOT EXISTS COMMENTS (
    comment_id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    author_id UUID NOT NULL,
    parent_comment_id UUID,
    content TEXT NOT NULL,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    likes_count INT NOT NULL DEFAULT 0 CHECK (likes_count >= 0),
    sub_comments_count INT NOT NULL DEFAULT 0 CHECK (sub_comments_count >= 0),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES POSTS(post_id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_comments_parent_comment FOREIGN KEY (parent_comment_id) REFERENCES COMMENTS(comment_id)
);

CREATE INDEX IF NOT EXISTS ix_comments_parent_comment_id ON COMMENTS (parent_comment_id);
CREATE INDEX IF NOT EXISTS ix_comments_creation_datetime ON COMMENTS (creation_datetime);