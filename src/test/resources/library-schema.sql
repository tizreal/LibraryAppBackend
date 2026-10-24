-- The production schema (see README "Data model"); Hibernate validates the entities against it.
CREATE TABLE books (
    book_id          SERIAL PRIMARY KEY,
    title            VARCHAR(100) NOT NULL,
    author           VARCHAR(100) NOT NULL,
    isbn             VARCHAR(13)  NOT NULL UNIQUE,
    publication_year INTEGER
);

CREATE TABLE users (
    user_id    SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    user_name  VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at TIMESTAMP   NOT NULL DEFAULT now()
);
