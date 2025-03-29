-- Создание таблицы чатов
CREATE TABLE chats
(
    id       SERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

-- Создание таблицы сообщений
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
