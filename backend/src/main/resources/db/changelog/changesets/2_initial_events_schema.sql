CREATE TABLE IF NOT EXISTS events (
                                      id SERIAL PRIMARY KEY,
                                      title TEXT NOT NULL,
                                      author_id BIGINT NOT NULL,
                                      date TIMESTAMP NOT NULL,
                                      FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_participants (
                                                  event_id BIGINT NOT NULL,
                                                  user_id BIGINT NOT NULL,
                                                  PRIMARY KEY (event_id, user_id),
                                                  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);