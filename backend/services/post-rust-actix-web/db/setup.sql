CREATE TABLE users (
    user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_name VARCHAR(20) NOT NULL,
    encrypted_password CHAR(60) NOT NULL,
    creation_time BIGINT UNSIGNED NOT NULL
);
CREATE UNIQUE INDEX idx_user_name ON users (user_name);

CREATE TABLE post (
    post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY NOT NULL,
    creator BIGINT UNSIGNED NOT NULL,
    creation_time BIGINT UNSIGNED NOT NULL,
    last_modified BIGINT UNSIGNED,
    title VARCHAR(255) NOT NULL,
    url_link TEXT,
    text_content TEXT,
    CONSTRAINT UC_title UNIQUE (title)
);
CREATE INDEX idx_creation_time_post_id ON post (creation_time DESC, post_id DESC);
CREATE INDEX idx_creator ON post (creator);
