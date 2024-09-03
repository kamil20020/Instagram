CREATE TABLE IF NOT EXISTS FOLLOWERS (
    follower_id UUID PRIMARY KEY,
    followed_id UUID NOT NULL,
    CONSTRAINT followers_PK PRIMARY KEY(follower_id, followed_id),
    CONSTRAINT fk_followers_following FOREIGN KEY(following_id) REFERENCES USERS(user_id)
);