# AppBoxeo API

A backend REST API for a boxing training management app. Admins manage content (training videos, workout routines) and users; boxers consume that content and manage their own workout timer configurations. Built as a portfolio project to demonstrate a complete, production-style backend — not a tutorial CRUD.

![CI](https://github.com/Jaimeml95/appboxeo-api/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

**Live demo:** [https://appboxeo-api.onrender.com/swagger-ui/index.html](https://appboxeo-api.onrender.com/swagger-ui/index.html)

> Note: the app runs on a free-tier instance, so the first request after a period of inactivity may take 30–60 seconds to respond while the service spins back up. Subsequent requests are fast.

---

## Table of Contents

- [What it does](#what-it-does)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Security](#security)
- [Testing](#testing)
- [Running locally](#running-locally)
- [API overview](#api-overview)
- [CI/CD](#cicd)
- [Roadmap](#roadmap)

## What it does

- **Auth**: Google Sign-In for boxers (account auto-provisioned with role `BOXER` on first login) plus a single local email/password login reserved for the seeded admin account.
- **Users**: admin manages existing accounts — listing, retrieval, role updates (e.g. promoting a boxer to `ADMIN`), deletion.
- **Videos**: training content (technique, strength, cardio, nutrition; YouTube or self-hosted), admin-managed.
- **Workouts** (`Workout`): full CRUD with nested `Exercise` sub-resources (sets, difficulty level: beginner/intermediate/advanced).
- **Timer configurations**: CRUD scoped to the authenticated user — each boxer only sees and manages their own.

## Tech stack

| Layer | Technology |
|---|---|
| Language / Framework | Java 21, Spring Boot 4.1.0 |
| Security | Spring Security 6+, stateless JWT authentication |
| Persistence | Spring Data JPA / Hibernate, PostgreSQL (prod), H2 in-memory (integration tests) |
| Build | Maven (with wrapper) |
| Containerization | Docker, Docker Compose (multi-stage build, JRE Alpine runtime) |
| API docs | springdoc-openapi (Swagger / OpenAPI 3) |
| CI | GitHub Actions |
| Testing | JUnit 5, Mockito, MockMvc, Spring Boot Test |

## Architecture

Classic layered design: `Controller → Service → Repository → Entity`, with request/response DTOs kept fully separate from JPA entities — entities are never exposed directly through the API.

**Main entities:**
- `User` — implements Spring Security's `UserDetails`, roles `BOXER` / `ADMIN`
- `Video` — training content (type: YouTube / own video)
- `Workout` with nested `Exercise` — workout routines with sets and difficulty
- `TimerConfiguration` — per-user workout timer settings

Errors are handled centrally via `@RestControllerAdvice` (`GlobalExceptionHandler`), returning semantically correct HTTP codes (401 invalid credentials, 403 forbidden, 404 not found, 409 conflict/duplicate, 400 validation).

## Security

- **Authentication**: two providers, no in-between. Boxers authenticate exclusively via Google Sign-In — the backend verifies the Google ID token's signature and audience server-side (`GoogleIdTokenVerifier`) and rejects unverified emails; the frontend's own claims are never trusted directly. The only local, password-based account is the seeded admin. Either path issues the same app-signed JWT, sent as a Bearer token on every protected request.
- **Authorization**: two layers — global route rules in `SecurityConfig` (e.g. `/api/v1/users/**` restricted to `ADMIN`, `/api/v1/timer-configurations/**` open to `BOXER` or `ADMIN`) plus method-level `@PreAuthorize` on write operations (create/edit/delete restricted to `ADMIN`).
- **Input validation**: Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Size`, etc.) on every request DTO.
- **No client-controlled roles**: new accounts are always created with role `BOXER` server-side on first Google login; promoting someone to `ADMIN` is a separate, authenticated admin action (`PUT /api/v1/users/{id}`) — a client can never request a role for itself.

**Two real issues found and fixed during development:**
1. The registration and user-management endpoints originally returned the `User` entity directly as JSON, leaking the bcrypt password hash and other internal Spring Security fields. Fixed with a dedicated `UserResponseDTO`; controller tests now explicitly assert sensitive fields never appear in any response.
2. Validation failures (`@Valid`) on public endpoints returned an opaque `403` instead of `400` in real deployment — the test suite (MockMvc) never caught it because it doesn't reproduce the container-level internal error dispatch that real Tomcat performs, which Spring Security was blocking. Fixed by explicitly permitting the `ERROR` dispatcher type in `SecurityConfig`. Found through manual end-to-end verification against a running Docker container, not through the automated tests.

> When deployed, the JWT secret and Google OAuth Client ID used in production are exclusive to that environment — never the placeholder values committed in `.env.example`.

## Testing

**83 automated tests** across three levels:

1. **Unit tests** (JUnit 5 + Mockito) — business logic in isolation, repositories mocked.
2. **Controller tests** (MockMvc + `@WebMvcTest`) — request/response validation, role-based access control, status codes, and explicit checks that sensitive data (passwords) never leaks in responses.
3. **Integration tests** (`@SpringBootTest` + in-memory H2) — full end-to-end flows: Google login (with a mocked token verifier) → account provisioning → real JWT → authenticated calls, against a real (in-memory) database, no mocks beyond the Google verification boundary.

```bash
./mvnw test
```

## Running locally

Requirements: Docker and Docker Compose.

```bash
git clone https://github.com/Jaimeml95/appboxeo-api.git
cd appboxeo-api
docker compose up --build
```

The API will be available at `http://localhost:8080`, with interactive documentation at `http://localhost:8080/swagger-ui/index.html`.

Environment variables (database credentials, JWT secret, etc.) are configured via `.env` — see `.env.example` for the required keys.

## API overview

Full interactive documentation is available via Swagger/OpenAPI once the app is running. Every endpoint documents its real response codes (`201`/`204`/`400`/`401`/`403`/`404`/`409`) with the matching error schema — reviewed manually endpoint by endpoint, not just the default `200` that springdoc generates automatically.

| Resource | Access |
|---|---|
| `POST /api/v1/auth/google` | Public — Google Sign-In, auto-provisions `BOXER` accounts |
| `POST /api/v1/auth/login` | Public — local login, admin account only |
| `GET/PUT/DELETE /api/v1/users/**` | `ADMIN` |
| `GET /api/v1/videos` | Authenticated |
| `POST/PUT/DELETE /api/v1/videos` | `ADMIN` |
| `/api/v1/workouts/**` incl. `/{id}/exercises` | Authenticated (writes: `ADMIN`) |
| `/api/v1/timer-configurations/**` | Owner (`BOXER`) or `ADMIN` |

## CI/CD

Every push and pull request to `main` triggers a GitHub Actions workflow that runs all 83 tests on a clean Ubuntu VM with JDK 21 — an independent, real verification, not just "works on my machine."

## Roadmap

- [x] Deploy to a public environment
- [ ] Add Docker image publishing to the CI pipeline
- [ ] Expand automated test coverage as new features are added

---

**Author:** Jaime Moro López — [github.com/Jaimeml95](https://github.com/Jaimeml95)
