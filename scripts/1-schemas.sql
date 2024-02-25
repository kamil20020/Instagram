CREATE TABLE IF NOT EXISTS USERS (
    user_id UUID PRIMARY KEY,
    user_account_id varchar(50) NOT NULL UNIQUE,
    nickname varchar(255) UNIQUE,
    firstname varchar(50),
    surname varchar(50),
    tel varchar(15),
    avatar BYTEA,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    description TEXT,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    followers INT NOT NULL DEFAULT 0,
    followings INT NOT NULL DEFAULT 0,
    number_of_posts INT NOT NULL DEFAULT 0
);

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

CREATE TABLE IF NOT EXISTS POSTS_LIKES(
    post_like_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    CONSTRAINT fk_posts_likes_user FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    CONSTRAINT fk_posts_likes_post FOREIGN KEY (post_id) REFERENCES POSTS(post_id)
);

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