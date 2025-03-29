CREATE TABLE message_reads
(
    id         SERIAL PRIMARY KEY,
    chat_id    BIGINT    NOT NULL,
    message_id BIGINT    NOT NULL,
    user_id    BIGINT    NOT NULL,
    read_at    TIMESTAMP NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE,
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

ALTER TABLE events
    ADD COLUMN status TEXT;