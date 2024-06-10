CREATE TABLE users
(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255),
    roles VARCHAR(255)
)