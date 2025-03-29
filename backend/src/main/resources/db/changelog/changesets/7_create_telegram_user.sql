CREATE TABLE IF NOT EXISTS telegram_users (
                                              id SERIAL PRIMARY KEY,
                                              telegram_username TEXT NOT NULL UNIQUE,
                                              telegram_chat_id BIGINT,
                                              user_id BIGINT NOT NULL,
                                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );