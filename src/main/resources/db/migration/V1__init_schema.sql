CREATE TABLE users (
    id       BIGSERIAL    PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE book (
    id       BIGSERIAL    PRIMARY KEY,
    title    VARCHAR(255),
    author   VARCHAR(255),
    quantity INTEGER
);

CREATE TABLE reservation (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT REFERENCES users(id),
    book_id          BIGINT REFERENCES book(id),
    reservation_date DATE,
    return_date      DATE,
    returned         BOOLEAN NOT NULL
);
