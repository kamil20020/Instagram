CREATE TABLE IF NOT EXISTS MESSAGES (
    message_id UUID PRIMARY KEY,
    sender_account_id VARCHAR(50) NOT NULL,
    receiver_account_id VARCHAR(50) NOT NULL,
    content VARCHAR(255) NOT NULL,
    creation_date timestamp NOT NULL,
    read boolean NOT NULL DEFAULT FALSE
);