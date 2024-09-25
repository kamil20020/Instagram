CREATE TABLE IF NOT EXISTS MESSAGES (
    message_id UUID PRIMARY KEY,
    sender_id UUID NOT NULL,
    receiver_id UUID NOT NULL,
    content VARCHAR(255) NOT NULL
);