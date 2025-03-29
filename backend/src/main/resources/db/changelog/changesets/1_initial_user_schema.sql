CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT        NOT NULL,
    name     TEXT        NOT NULL,
    email    TEXT UNIQUE NOT NULL,
    role     TEXT        NOT NULL
);