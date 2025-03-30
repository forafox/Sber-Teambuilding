CREATE TABLE IF NOT EXISTS money_transfer
(
    id           SERIAL PRIMARY KEY,
    amount       DOUBLE PRECISION NOT NULL,
    sender_id    BIGINT           NOT NULL,
    recipient_id BIGINT           NOT NULL,
    event_id     BIGINT           NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);