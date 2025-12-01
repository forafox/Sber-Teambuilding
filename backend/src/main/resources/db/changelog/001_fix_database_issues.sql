-- =============================================
-- Исправление проблем базы данных
-- =============================================

-- 2.2.1 Дублирование таблиц и колонок
-- Удаляем старые дублирующие таблицы в правильном порядке (2.2.2)
DROP TABLE IF EXISTS message_reads;
DROP TABLE IF EXISTS option_voters;
DROP TABLE IF EXISTS money_transfer;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS chats;

-- 2.2.3, 2.2.5, 2.2.8 Стандартизация и оптимизация таблицы users
COMMENT ON TABLE users IS 'Пользователи системы';
ALTER TABLE users
ALTER COLUMN username TYPE VARCHAR(100),
    ALTER COLUMN password TYPE VARCHAR(255),
    ALTER COLUMN name TYPE VARCHAR(150),
    ALTER COLUMN email TYPE VARCHAR(255),
    ALTER COLUMN role TYPE VARCHAR(50);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- 2.2.3, 2.2.5, 2.2.8 Стандартизация таблицы events
COMMENT ON TABLE events IS 'События/мероприятия';
ALTER TABLE events
ALTER COLUMN title TYPE VARCHAR(255),
    ALTER COLUMN description TYPE VARCHAR(1000),
    ALTER COLUMN location TYPE VARCHAR(500),
    ALTER COLUMN status TYPE VARCHAR(50);
DROP INDEX IF EXISTS idx_events_date;
CREATE INDEX idx_events_date ON events(date);
CREATE INDEX idx_events_author_id ON events(author_id);
CREATE INDEX idx_events_status ON events(status);

-- Удаляем дублирующую колонку chat_id из events (2.2.6)
ALTER TABLE events DROP COLUMN IF EXISTS chat_id;

-- 2.2.4 Добавляем индексы для event_participants
COMMENT ON TABLE event_participants IS 'Участники событий';
CREATE INDEX idx_event_participants_user_id ON event_participants(user_id);
CREATE INDEX idx_event_participants_event_id ON event_participants(event_id);

-- 2.2.3, 2.2.5, 2.2.8 Стандартизация таблицы tasks
COMMENT ON TABLE tasks IS 'Задачи';
ALTER TABLE tasks
ALTER COLUMN title TYPE VARCHAR(255),
    ALTER COLUMN status TYPE VARCHAR(50),
    ALTER COLUMN description TYPE VARCHAR(2000),
    ALTER COLUMN url TYPE VARCHAR(500);
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);
CREATE INDEX idx_tasks_author_id ON tasks(author_id);
CREATE INDEX idx_tasks_event_id ON tasks(event_id);
CREATE INDEX idx_tasks_status ON tasks(status);

-- 2.2.6 Создаем финальную версию таблицы chats с явной связью
COMMENT ON TABLE chats IS 'Чаты для событий';
CREATE TABLE chats (
                       id SERIAL PRIMARY KEY,
                       event_id BIGINT NOT NULL UNIQUE,
                       FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);
CREATE INDEX idx_chats_event_id ON chats(event_id);

-- 2.2.3, 2.2.5, 2.2.8 Стандартизация таблицы messages
COMMENT ON TABLE messages IS 'Сообщения в чатах';
CREATE TABLE messages (
                          id SERIAL PRIMARY KEY,
                          chat_id BIGINT NOT NULL,
                          author_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
                          reply_to_message_id BIGINT,
                          poll_id BIGINT UNIQUE,
                          pinned BOOLEAN NOT NULL DEFAULT FALSE,
                          FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
                          FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (reply_to_message_id) REFERENCES messages(id) ON DELETE SET NULL
);
CREATE INDEX idx_messages_chat_id ON messages(chat_id);
CREATE INDEX idx_messages_author_id ON messages(author_id);
CREATE INDEX idx_messages_timestamp ON messages(timestamp);
CREATE INDEX idx_messages_reply_to ON messages(reply_to_message_id) WHERE reply_to_message_id IS NOT NULL;

-- 2.2.5, 2.2.8 Стандартизация таблицы telegram_users
COMMENT ON TABLE telegram_users IS 'Привязка Telegram аккаунтов';
ALTER TABLE telegram_users
ALTER COLUMN telegram_username TYPE VARCHAR(100);
CREATE INDEX idx_telegram_users_user_id ON telegram_users(user_id);
CREATE INDEX idx_telegram_users_username ON telegram_users(telegram_username);

-- 2.2.5, 2.2.8 Стандартизация таблиц для опросов
COMMENT ON TABLE polls IS 'Опросы в сообщениях';
ALTER TABLE polls
ALTER COLUMN title TYPE VARCHAR(255),
    ALTER COLUMN poll_type TYPE VARCHAR(50);

COMMENT ON TABLE options IS 'Варианты ответов в опросах';
ALTER TABLE options
ALTER COLUMN title TYPE VARCHAR(255);

COMMENT ON TABLE option_voters IS 'Голоса пользователей в опросах';
CREATE TABLE option_voters (
                               option_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               PRIMARY KEY (option_id, user_id),
                               FOREIGN KEY (option_id) REFERENCES options(id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_option_voters_user_id ON option_voters(user_id);

-- 2.2.4, 2.2.8 Добавляем таблицу message_reads с индексами
COMMENT ON TABLE message_reads IS 'Отметки о прочтении сообщений';
CREATE TABLE message_reads (
                               id SERIAL PRIMARY KEY,
                               chat_id BIGINT NOT NULL,
                               message_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               read_at TIMESTAMP NOT NULL,
                               UNIQUE (message_id, user_id),
                               FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
                               FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_message_reads_user_chat ON message_reads(user_id, chat_id);
CREATE INDEX idx_message_reads_message_id ON message_reads(message_id);

-- 2.2.8 Стандартизация таблицы money_transfer
COMMENT ON TABLE money_transfer IS 'Денежные переводы между участниками событий';
CREATE TABLE money_transfer (
                                id SERIAL PRIMARY KEY,
                                amount DOUBLE PRECISION NOT NULL CHECK (amount > 0),
                                sender_id BIGINT NOT NULL,
                                recipient_id BIGINT NOT NULL,
                                event_id BIGINT NOT NULL,
                                transfer_date TIMESTAMP DEFAULT NOW(),
                                FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);
CREATE INDEX idx_money_transfer_sender ON money_transfer(sender_id);
CREATE INDEX idx_money_transfer_recipient ON money_transfer(recipient_id);
CREATE INDEX idx_money_transfer_event ON money_transfer(event_id);

-- 2.2.7 Добавляем таблицу для истории изменений
COMMENT ON TABLE audit_log IS 'Журнал аудита важных изменений';
CREATE TABLE audit_log (
                           id SERIAL PRIMARY KEY,
                           table_name VARCHAR(100) NOT NULL,
                           record_id BIGINT NOT NULL,
                           action VARCHAR(20) NOT NULL CHECK (action IN ('INSERT', 'UPDATE', 'DELETE')),
    old_values JSONB,
    new_values JSONB,
    changed_by BIGINT,
    changed_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at);

-- 2.2.8 Добавляем стандартные комментарии
COMMENT ON COLUMN users.username IS 'Уникальное имя пользователя для входа';
COMMENT ON COLUMN users.email IS 'Уникальный email пользователя';
COMMENT ON COLUMN users.role IS 'Роль пользователя (admin, user, etc.)';

COMMENT ON COLUMN events.author_id IS 'Создатель события';
COMMENT ON COLUMN events.date IS 'Дата и время проведения события';
COMMENT ON COLUMN events.status IS 'Статус события (planned, active, completed, cancelled)';

COMMENT ON COLUMN tasks.assignee_id IS 'Исполнитель задачи';
COMMENT ON COLUMN tasks.author_id IS 'Автор задачи';
COMMENT ON COLUMN tasks.event_id IS 'Событие, к которому привязана задача';

COMMENT ON COLUMN messages.reply_to_message_id IS 'ID сообщения, на которое дан ответ';
COMMENT ON COLUMN messages.poll_id IS 'Прикрепленный опрос';

COMMENT ON COLUMN telegram_users.telegram_chat_id IS 'ID чата в Telegram для уведомлений';

COMMENT ON COLUMN polls.poll_type IS 'Тип опроса (single, multiple)';

-- 2.2.8 Добавляем стандартные ограничения
ALTER TABLE events ADD CONSTRAINT ck_event_status
    CHECK (status IN ('planned', 'active', 'completed', 'cancelled'));

ALTER TABLE tasks ADD CONSTRAINT ck_task_status
    CHECK (status IN ('todo', 'in_progress', 'review', 'done', 'cancelled'));

-- 2.2.3 Исправляем потенциальные проблемы с внешними ключами
-- Убеждаемся, что все внешние ключи имеют индексы
DO $$
BEGIN
    -- Проверяем и создаем недостающие индексы для FK
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_events_chat_id') THEN
CREATE INDEX idx_events_chat_id ON chats(event_id);
END IF;
END $$;