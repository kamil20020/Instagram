CREATE TABLE IF NOT EXISTS USERS (
    user_id UUID PRIMARY KEY,
    account_id varchar(50) NOT NULL,
    nickname varchar(255),
    firstname varchar(50),
    surname varchar(50),
    tel varchar(15),
    avatar BYTEA,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    description TEXT,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    followers_count INT NOT NULL DEFAULT 0 CHECK (followers_count > 0),
    followings_count INT NOT NULL DEFAULT 0 CHECK (followings_count > 0),
    posts_count INT NOT NULL DEFAULT 0 CHECK (posts_count > 0),
    CONSTRAINT uq_users_account_id UNIQUE (account_id),
    CONSTRAINT uq_users_nickname UNIQUE (nickname)
);

CREATE TABLE IF NOT EXISTS POSTS (
    post_id UUID PRIMARY KEY,
    author_id UUID NOT NULL,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    description TEXT,
    content BYTEA NOT NULL,
    are_hidden_likes BOOLEAN NOT NULL DEFAULT FALSE,
    are_disabled_comments BOOLEAN NOT NULL DEFAULT FALSE,
    likes_count INT NOT NULL DEFAULT 0 CHECK (likes_count > 0),
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES USERS(user_id)
);

CREATE IF NOT EXISTS INDEX ix_posts_author_id ON POSTS (author_id);
CREATE IF NOT EXISTS INDEX ix_posts_creation_datetime ON POSTS (creation_datetime);


CREATE TABLE IF NOT EXISTS POSTS_LIKES(
    post_like_id UUID PRIMARY KEY,
    author_id UUID NOT NULL,
    post_id UUID NOT NULL,
    CONSTRAINT fk_posts_likes_author FOREIGN KEY (author_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_posts_likes_post FOREIGN KEY (post_id) REFERENCES POSTS(post_id)
);

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

CREATE IF NOT EXISTS ix_comments_parent_comment_id ON COMMENTS (parent_comment_id);
CREATE IF NOT EXISTS INDEX ix_comments_creation_datetime ON COMMENTS (creation_datetime);


CREATE TABLE IF NOT EXISTS COMMENTS_LIKES (
    comment_like_id UUID PRIMARY KEY,
    comment_id UUID NOT NULL,
    author_id UUID NOT NULL,
    CONSTRAINT fk_comments_likes_comment FOREIGN KEY (comment_id) REFERENCES COMMENTS(comment_id),
    CONSTRAINT fk_comments_likes_author FOREIGN KEY (author_id) REFERENCES USERS(user_id)
);

