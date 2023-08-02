CREATE TABLE IF NOT EXISTS USERS (
    user_id UUID PRIMARY KEY,
    user_account_id UUID NOT NULL UNIQUE,
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