# Spring Boot Demo Project

This is a basic Spring Boot project created with Maven.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

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

### Redis Mode
Single node: simplest for local dev; uses `docker-compose.yml` → service `redis` (localhost:6379). App runs with default config from `application.properties`.

### Redis Cluster + MySQL (6 services)
This setup runs 6 independent Redis containers and then bootstraps them into a cluster (3 masters + 3 replicas), alongside MySQL.

Start and bootstrap
```bash
# Start containers
docker compose -f docker-compose.redis-cluster-sql.yml up -d

# Bootstrap the cluster (creates slots and replicas)
docker compose -f docker-compose.redis-cluster-sql.yml exec -T redis-7000 \
  redis-cli --cluster create \
  redis-7000:7000 redis-7001:7001 redis-7002:7002 \
  redis-7003:7003 redis-7004:7004 redis-7005:7005 \
  --cluster-replicas 1 --cluster-yes

# Verify
docker compose -f docker-compose.redis-cluster-sql.yml exec -T redis-7000 redis-cli -c -p 7000 cluster info
docker compose -f docker-compose.redis-cluster-sql.yml exec -T redis-7000 sh -lc "redis-cli -c -p 7000 cluster nodes | head"

# Stop (keep volumes)
docker compose -f docker-compose.redis-cluster-sql.yml down

# Remove containers and volumes
docker compose -f docker-compose.redis-cluster-sql.yml down -v
```

Run the Spring app in cluster mode (see `src/main/resources/application-cluster.properties`). When running the app inside the same Compose network, use the service names for cluster nodes:

```bash
# Example (inside Docker or via env override)
SPRING_REDIS_CLUSTER_NODES=redis-7000:7000,redis-7001:7001,redis-7002:7002,redis-7003:7003,redis-7004:7004,redis-7005:7005 \
mvn spring-boot:run -Dspring-boot.run.profiles=cluster
```

Important:
- Do not run the single-node `redis` from `docker-compose.yml` at the same time; ports would conflict.
- In this 6‑service setup, Redis nodes announce their container hostnames (e.g., `redis-7000`). For clients running on the host (outside Docker), MOVED redirects won’t resolve. Run the Spring app inside the same Docker network (recommended) or use a custom network setup that makes those hostnames resolvable from the host.
- MySQL in this compose uses the same defaults as the single-node compose (DB `appdb`, user `appuser`, password `apppass`).

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

Once the application is running:
- http://localhost:8080/health — Public health check
- http://localhost:8080/swagger-ui.html — Swagger UI (OpenAPI docs)
  - OpenAPI JSON: http://localhost:8080/v3/api-docs
- Authenticated hello:
  1) Create a user: `curl -i -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"email":"alice@example.com","firstName":"Alice","lastName":"Doe","password":"secret"}'`
  2) Login and store cookie: `curl -i -c cookies.txt -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"alice@example.com","password":"secret"}'`
  3) Call hello with session: `curl -i -b cookies.txt http://localhost:8080/`

## API Endpoints

- `POST /api/users` — Create user (JSON: email, firstName, lastName, password)
- `POST /api/auth/login` — Login with email/password, creates Redis-backed session, returns session + user summary
- `GET /api/auth/me` — Returns current session’s user info
- `POST /api/auth/logout` — Invalidates current session
- `GET /` — Hello message (requires authenticated session)
- `GET /health` — Health check (public)

## Swagger / OpenAPI

- Library: `springdoc-openapi-starter-webmvc-ui`
- UI: visit `http://localhost:8080/swagger-ui.html` (redirects to `/swagger-ui/index.html`).
- JSON: `http://localhost:8080/v3/api-docs`.

Notes:
- The `SessionAuthInterceptor` excludes Swagger endpoints so docs are publicly accessible.
- API metadata is defined via `@OpenAPIDefinition` on `src/main/java/com/redis/demo/DemoApplication.java`.

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Test (for testing)
 
## Docker

- `docker-compose.yml` runs a Redis 7 Alpine container with AOF persistence and a named volume `redis-data`.
- No password is configured; set `spring.redis.password` if you decide to protect Redis in the future.

## Configuration

The application runs on port 8080 by default. You can modify this in `src/main/resources/application.properties`.

## Current Status

- Architecture: Ports & Adapters (Hexagonal) with three layers — `domain` (business logic), `presentation` (controllers/DTOs), and `infrastructure` (persistence, crypto). MapStruct maps between layers.
- Persistence: Spring Data JDBC to MySQL; Flyway migration `V1__create_users_table.sql` creates the `users` table.
- Security (web): Spring Session on Redis stores HTTP sessions. `SessionAuthInterceptor` enforces authentication for all endpoints except `/api/auth/**`, `/health`, and Swagger docs (`/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`).
- Endpoints: `POST /api/users` (create user, hashes password with BCrypt), `POST /api/auth/login` (validate email/password), `GET /api/auth/me`, `POST /api/auth/logout`, `GET /health` (public), `GET /` (hello, now requires an authenticated session).
- Infrastructure: Persistence entity moved to `infrastructure/persistence/entity/UserEntity`, repository to `infrastructure/persistence/repository/UserRepository`, and adapter `infrastructure/persistence/UserRepositoryAdapter` implements `UserRepositoryPort`.
- Tooling: MapStruct configured via Maven; BCrypt via `spring-security-crypto`.

## Future Action Plan

1. Token-based authentication (mobile/API)
   - Access token: short‑lived JWT (5–15 min) signed and verified statelessly by the API.
   - Refresh token: long‑lived opaque token stored in Redis (hashed) with TTL and rotation.
   - Endpoints: `POST /api/auth/token` (login → access+refresh), `POST /api/auth/refresh`, `POST /api/auth/logout` (revoke current), `POST /api/auth/logout-all` (revoke all for user).
   - Redis schema: `rt:{userId}:{deviceId}:{id}` → `{ hash, scope, createdAt }` with TTL; `rtidx:{userId}` set for “logout all”; optional access-token denylist `blk:access:{jti}` for immediate revocation until expiry.
   - Security chains: Keep session-based auth for web routes; add a JWT filter chain for `/api/**`.

2. Session improvements (optional)
   - Principal indexing: set `FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME` at login; on user delete/disable, delete all sessions for that principal.
   - Revocation check: add `exists/enabled` verification for `userId` in interceptor (cache result for 30–60s) to handle mid-session user deletion.

3. Domain features
   - Add more use cases (e.g., find user by email, profile update) as domain services behind ports; map via MapStruct as done for create.

4. Observability & hardening
   - Add structured logging for auth flows, rate limiting on login/refresh, and basic health/readiness probes for Redis/MySQL.
 
5. Cluster-based login fix
   - Current status: login with Redis Cluster profile is not working; session writes fail when the app runs on the host due to cluster MOVED redirects to container hostnames (e.g., redis-7000).
   - Fix options: run the Spring app inside the same Docker Compose network, or adjust Redis nodes to announce a host-reachable address (e.g., set `--cluster-announce-ip` to host IP and use `localhost:700x`), and verify Lettuce cluster topology refresh settings.
 
 
