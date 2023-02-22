CREATE TABLE IF NOT EXISTS USERS (
    user_id serial PRIMARY KEY,
    user_account_id int NOT NULL UNIQUE,
    nickname varchar(255) NOT NULL UNIQUE,
    firstname varchar(50) NOT NULL,
    surname varchar(50) NOT NULL,
    email varchar(62) NOT NULL,
    tel varchar(15) NOT NULL,
    avatar bytea,
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL
);