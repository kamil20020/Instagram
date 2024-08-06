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