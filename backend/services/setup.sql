CREATE TABLE users (
    user_id BINARY(16) NOT NULL PRIMARY KEY,
    user_name VARCHAR(20) NOT NULL,
    encrypted_password CHAR(60) NOT NULL,
    creation_time BIGINT UNSIGNED NOT NULL
);
CREATE UNIQUE INDEX idx_user_name
ON users (user_name);

CREATE TABLE post (
    post_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    creator BIGINT UNSIGNED NOT NULL,
    creation_time BIGINT UNSIGNED NOT NULL,
    last_modified BIGINT UNSIGNED,
    title VARCHAR(255) NOT NULL,
    url_link TEXT,
    text_content TEXT,
    CONSTRAINT UC_title UNIQUE (title)
);
CREATE INDEX idx_creator
ON post (creator);
CREATE INDEX idx_last_modified
ON post (last_modified);

CREATE TABLE admins (
    admin_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    encrypted_password CHAR(60) NOT NULL
);
