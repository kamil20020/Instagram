CREATE TABLE IF NOT EXISTS COMMENTS (
    comment_id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    parent_comment_id UUID,
    content TEXT NOT NULL,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES POSTS(post_id),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_comments_parent_comment FOREIGN KEY (parent_comment_id) REFERENCES COMMENTS(comment_id)
);