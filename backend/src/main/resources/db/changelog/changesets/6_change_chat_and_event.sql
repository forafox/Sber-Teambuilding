DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS chats;

CREATE TABLE chats
(
    id SERIAL PRIMARY KEY
);

CREATE TABLE messages
(
    id        SERIAL PRIMARY KEY,
    chat_id   BIGINT    NOT NULL,
    author_id BIGINT    NOT NULL,
    content   TEXT      NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);


ALTER TABLE events
    ADD COLUMN chat_id BIGINT UNIQUE;
ALTER TABLE events
    ADD CONSTRAINT fk_event_chat FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE;