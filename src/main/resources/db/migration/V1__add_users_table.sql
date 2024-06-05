CREATE TABLE users
(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(255),
    password VARCHAR(255),
    roles VARCHAR(255)
)