# Library Management System — Spring Boot API

REST API for a small library management system: create, list, update and delete books and
library members. It is the backend the React frontend is built against; a second backend
implements the same endpoints with Express.

**Live demo:** [library-app-hazel-psi.vercel.app](https://library-app-hazel-psi.vercel.app)
(runs on this API; a shared public sandbox) · **Frontend:**
[tizreal/LibraryApp](https://github.com/tizreal/LibraryApp) · **Express version:**
[tizreal/LibraryAppExpressBackend](https://github.com/tizreal/LibraryAppExpressBackend)

The API runs on Render's free tier and sleeps when idle, so the first request can take a
minute or two.

## Tech stack

Java 17 · Spring Boot 4 · Spring Web MVC · Spring Data JPA · PostgreSQL · Docker

## Design

- Layered structure: controllers → services → Spring Data repositories.
- Hibernate runs in `validate` mode: the app checks at startup that the entities match the
  database schema and refuses to start on a mismatch, instead of altering the schema.
- JSON uses snake_case field names. (The members username field is `user_name_string`
  here and `user_name` in the Express version.)
- `GET` by id returns `404` when the record doesn't exist.
- CORS allows the local Vite dev server and Vercel deployments.

## API

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/books` | List books |
| `GET` | `/api/books/{bookId}` | Get one book |
| `POST` | `/api/books` | Create a book |
| `PUT` | `/api/books/{bookId}` | Update a book |
| `DELETE` | `/api/books/{bookId}` | Delete a book |
| `GET` | `/api/users` | List members |
| `GET` | `/api/users/{userId}` | Get one member |
| `POST` | `/api/users` | Create a member |
| `PUT` | `/api/users/{userId}` | Update a member |
| `DELETE` | `/api/users/{userId}` | Delete a member |

## Data model

Verified against PostgreSQL 16: the application starts with Hibernate schema validation
and creates books and members on this schema, which the Express version also uses.

```sql
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
```

## Running locally

Requirements: Java 17 and a PostgreSQL database with the tables above.

```bash
export PGHOST=localhost PGPORT=5432 PGDATABASE=librarydb PGUSER=postgres PGPASSWORD=postgres
./mvnw spring-boot:run          # http://localhost:8080
```

The connection uses `sslmode=require`, as hosted PostgreSQL providers expect; for a local
database without SSL, remove that parameter from `spring.datasource.url` in
`src/main/resources/application.properties`.

A multi-stage `Dockerfile` builds and runs the JAR for deployment.

## Project structure

```
src/main/java/com/tca/libraryApp/
├── controller/   REST endpoints for books and members
├── entity/       JPA entities
├── repository/   Spring Data repositories
├── service/      business logic
└── CorsConfig.java
```
