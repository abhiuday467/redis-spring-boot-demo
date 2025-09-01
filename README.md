# Spring Boot Demo Project

This is a basic Spring Boot project created with Maven.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/redis/demo/
│   │       ├── DemoApplication.java
│   │       └── controller/
│   │           └── HelloController.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/redis/demo/
            └── DemoApplicationTests.java
```

## Running the Application

### Using Maven
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Or build and run the JAR
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Using IDE
Run the `DemoApplication.java` class directly from your IDE.

### Using Docker Compose (Redis)
This project includes a `docker-compose.yml` to run Redis locally with persistence (no password).

```bash
# Start Redis in the background
docker compose up -d redis

# Check status and logs
docker compose ps
docker compose logs -f redis

# Stop (keep data volume)
docker compose down

# Remove containers and data volume
docker compose down -v
```

Notes:
- The app is configured to connect to `localhost:6379` by default, which matches the compose setup.
- If you later run the Spring app in Docker, use `SPRING_REDIS_HOST=redis` so it can reach the `redis` service over the compose network.

### Using Docker Compose (MySQL)
This repository's `docker-compose.yml` also includes a MySQL 8 service with a persistent volume and a default database/user.

Defaults (from compose):
- Host: `localhost` (or `mysql` from other compose services)
- Port: `3306`
- Database: `appdb`
- User: `appuser`
- Password: `apppass`
- Root password: `example`

Commands
```bash
# (Optional) Pre-pull the MySQL image
docker pull mysql:8.0

# Start only MySQL
docker compose up -d mysql

# Check status and logs
docker compose ps
docker compose logs -f mysql

# Access MySQL interactive client (app user)
docker compose exec mysql mysql -u appuser -p appdb
# When prompted, enter password: apppass

# Verify connectivity (as app user)
docker compose exec mysql mysql -uappuser -papppass -e "SHOW DATABASES;"

# Verify connectivity (as root)
docker compose exec mysql mysql -uroot -pexample -e "SELECT VERSION();"

# Stop (keep data volume)
docker compose down

# Remove containers and data volumes (Redis + MySQL)
docker compose down -v
```

Notes:
- Spring Boot datasource (added in a later step) should use `jdbc:mysql://localhost:3306/appdb` with `username=appuser` and `password=apppass` for local development.
- When the Spring app runs inside the same compose network, use `jdbc:mysql://mysql:3306/appdb` for the URL.

## Testing

Once the application is running, you can test it by visiting:
- http://localhost:8080/ - Should display "Hello, Spring Boot!"

## API Endpoints

- `GET /` - Returns a greeting message

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Test (for testing)
 
## Docker

- `docker-compose.yml` runs a Redis 7 Alpine container with AOF persistence and a named volume `redis-data`.
- No password is configured; set `spring.redis.password` if you decide to protect Redis in the future.

## Configuration

The application runs on port 8080 by default. You can modify this in `src/main/resources/application.properties`.

## Current Status

- Server: Redis 7 (Alpine) via Docker Compose with AOF persistence and a named volume (`redis-data`); no password/auth configured.
- Client: Spring Data Redis using Lettuce (Spring Boot default) and Spring Session storing HTTP session data in Redis.
- Caching: `@EnableCaching` is enabled, but no methods use `@Cacheable`/`@CachePut`/`@CacheEvict` yet.
- Connection: App points to `localhost:6379` with empty password as configured in `src/main/resources/application.properties`.
- Data flow: Session → Spring Session → Spring Data Redis → Lettuce → Redis.

## Future Action Plan

1. Have MySQL database
   - Add a MySQL service (Docker Compose) with a persistent volume and default database/schema.
   - Add MySQL driver dependency to `pom.xml` and configure `spring.datasource.*`.

2. Create `user` table
   - Columns: `email_id` (unique), `first_name`, `last_name`, `password_hash`.
   - Use JPA entity + schema migration (Flyway/Liquibase) or DDL auto for initial setup.
   - Store only hashed passwords (BCrypt) — never plaintext.

3. Endpoint to create user
   - `POST /api/users` accepts email, firstName, lastName, password.
   - Hash password with BCrypt and persist via a `UserRepository`.
   - Validate uniqueness of `email_id`; return 201 on success.

4. Endpoint to validate user and create session
   - `POST /api/auth/login` verifies email + password (BCrypt match).
   - On success, create Spring Session (Redis-backed) with minimal user info.
   - Return session identifier and user summary (no sensitive fields).

5. Protect other endpoints with this session
   - Add a session filter or use Spring Security to require an authenticated session.
   - Expose `GET /api/auth/me` for current user; block access when no valid session.
   - Optionally add logout endpoint to invalidate session.
