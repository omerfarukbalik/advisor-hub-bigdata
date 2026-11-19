# Advisor Hub — Big Data

A Spring Boot web app for managing thesis advisers, studies and student supervisions. It brings together four big-data and AI tools in one small CRUD app:

| Concern | Technology |
|---|---|
| Relational data | **PostgreSQL** (Spring Data JPA) |
| Caching | **Redis** (Spring Cache, 10 min TTL) |
| Image storage | **HDFS** (Hadoop client 3.4.1) |
| AI chat | **Ollama** running `llama3.2` locally |

Server-side rendered with **Thymeleaf**. Built with **Java 17**, **Spring Boot 4.1** and **Gradle**.

---

## Features

- **Supervisions** (`/`): list, create, edit and delete supervision records. The list page is a JOIN of advisers, studies and supervisions.
- **Advisers** (`/advisers`): CRUD for advisers, with photo upload. The image bytes go to HDFS and only the file name is saved in PostgreSQL.
- **Images** (`/images/{name}`): streams photos straight from HDFS. Responses carry an `X-Served-From: HDFS` header, which you can check in the browser's Network tab.
- **Chat** (`/chat`): a chat page backed by a local Llama 3.2 model through Ollama. The whole conversation is sent on each turn, so the model keeps context.
- **Redis caching** with explicit eviction: saving or deleting an adviser also clears the supervision list cache. Cache hits and misses are logged at `TRACE` level.

## Architecture

```
Browser ──► Spring Boot (:8090)
              ├── JPA ─────────► PostgreSQL (:5432)   advisers, studies, supervises
              ├── Spring Cache ► Redis (:6379)        advisers, adviser, studies,
              │                                       supervision, supervisionView
              ├── Hadoop FS ───► HDFS (:9000)         /project2/images/*
              └── RestClient ──► Ollama (:11434)      llama3.2
```

### Project layout

```
src/main/java/com/sau/project2/
├── config/       HdfsConfig: HDFS FileSystem bean (replication = 1)
├── controller/   Adviser, Supervision, Image, Chat
├── dto/          SupervisionView (JOIN projection), ChatMessage
├── model/        Adviser, Study, Supervision (JPA entities)
├── repository/   Spring Data JPA repositories
└── service/      Business logic + caching, HdfsService, OllamaService
src/main/resources/
├── application.properties
└── templates/    Thymeleaf views
```

## Prerequisites

- Java 17
- PostgreSQL
- Redis
- Hadoop (single-node HDFS)
- [Ollama](https://ollama.com) with the model pulled: `ollama pull llama3.2`

## Database setup

The schema is **not** created automatically (`spring.jpa.hibernate.ddl-auto=none`), so create it yourself in a database called `project2`. Here is a schema that matches the entities:

```sql
CREATE TABLE advisers (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255),
    department VARCHAR(255),
    photo      VARCHAR(255)          -- HDFS file name
);

CREATE TABLE studies (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255),
    description TEXT
);

CREATE TABLE supervises (
    id          SERIAL PRIMARY KEY,
    adviser_id  INTEGER REFERENCES advisers(id) ON DELETE CASCADE,
    study_id    INTEGER REFERENCES studies(id)  ON DELETE CASCADE,
    student     VARCHAR(255),
    performance VARCHAR(255)
);
```

The app has no UI for studies, so add them with SQL:

```sql
INSERT INTO studies (title, description) VALUES ('Sample Study', 'Description');
```

## HDFS setup

```bash
hdfs dfs -mkdir -p /project2/images
```

## Configuration

All settings are in `src/main/resources/application.properties`:

| Property | Default |
|---|---|
| `server.port` | `8090` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/project2` |
| `spring.datasource.username` / `password` | `postgres` / `postgres` |
| `spring.data.redis.host` / `port` | `localhost` / `6379` |
| `spring.cache.redis.time-to-live` | `10m` |
| `hdfs.uri` | `hdfs://localhost:9000` |
| `hdfs.user` | `merba` (change this to your HDFS user) |
| `hdfs.image-dir` | `/project2/images` |
| `ollama.url` | `http://localhost:11434` |
| `ollama.model` | `llama3.2` |
| Max upload size | `10MB` |

## Running

```bash
./gradlew bootRun
```

Then open <http://localhost:8090>.

| Route | Page |
|---|---|
| `/` | Supervision list |
| `/supervision/new` | New supervision |
| `/advisers` | Adviser list |
| `/adviser/new` | New adviser (with photo) |
| `/chat` | Chat with Llama 3.2 |

## Caching strategy

| Cache | Key | Filled by | Cleared when |
|---|---|---|---|
| `supervisionView` | `all` | Supervision list (JOIN) | Any supervision or adviser save/delete |
| `supervision` | `id` | Edit form | Supervision delete, adviser delete |
| `advisers` | `all` | Adviser list | Adviser save/delete |
| `adviser` | `id` | Adviser edit form | Adviser delete (refreshed on save) |
| `studies` | `all` | Dropdowns | Never (TTL only) |
