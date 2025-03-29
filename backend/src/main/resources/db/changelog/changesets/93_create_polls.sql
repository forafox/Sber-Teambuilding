-- Create polls table
CREATE TABLE polls
(
    id        SERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    poll_type VARCHAR(50)  NOT NULL
);

-- Create options table with poll_id as foreign key
CREATE TABLE options
(
    id      SERIAL PRIMARY KEY,
    title   VARCHAR(255) NOT NULL,
    poll_id BIGINT,
    FOREIGN KEY (poll_id) REFERENCES polls (id) ON DELETE CASCADE
);

-- Create junction table for option voters
CREATE TABLE option_voters
(
    option_id BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    PRIMARY KEY (option_id, user_id),
    FOREIGN KEY (option_id) REFERENCES options (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Add poll_id column to messages table
ALTER TABLE messages
    ADD COLUMN poll_id BIGINT UNIQUE,
ADD CONSTRAINT fk_message_poll 
    FOREIGN KEY (poll_id) REFERENCES polls(id) ON
DELETE
SET NULL;