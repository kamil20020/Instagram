CREATE TABLE IF NOT EXISTS USERS (
    user_id UUID PRIMARY KEY,
    user_account_id UUID NOT NULL UNIQUE,
    nickname varchar(255) NOT NULL UNIQUE,
    firstname varchar(50) NOT NULL,
    surname varchar(50) NOT NULL,
    email varchar(62) NOT NULL,
    tel varchar(15),
    avatar bytea,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    description TEXT,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    followers INT NOT NULL DEFAULT 0,
    followings INT NOT NULL DEFAULT 0
);