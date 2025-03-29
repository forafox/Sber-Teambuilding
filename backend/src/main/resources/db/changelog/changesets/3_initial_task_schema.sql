CREATE TABLE tasks
(
    id          SERIAL PRIMARY KEY,
    title       TEXT        NOT NULL,
    assignee_id BIGINT,
    status      TEXT        NOT NULL,
    description TEXT,
    expenses    DOUBLE PRECISION,
    author_id   BIGINT      NOT NULL,
    FOREIGN KEY (assignee_id) REFERENCES users (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);