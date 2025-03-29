ALTER TABLE tasks
    ADD COLUMN event_id BIGINT,
    ADD CONSTRAINT fk_tasks_event FOREIGN KEY (event_id) REFERENCES events (id);
