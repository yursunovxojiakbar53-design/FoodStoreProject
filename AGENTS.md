<!-- AGENTS.md: Instructions for AI coding agents working on this repository -->
# AGENTS — Quick onboarding for code-writing agents

Checklist (what I'll do first)
- Understand auth model (JWT + permissions claim)
- Respect AOP permission checks (@RequirePermission + Perms)
- Build/run with the Maven wrapper (Windows examples below)
- Use Java 17 and enable Lombok annotation processing

Key project facts (big picture)
- Spring Boot backend (Spring Boot 4.0.6, Java 17). Entry: `src/main/java/org/example/project/ProjectApplication.java`.
- Layered structure: controllers -> services -> repositories. DTOs in `dto/`, entities in `entity/`, repos in `repository/`.
- Security: stateless JWTs. Important files:
  - `src/main/java/org/example/project/security/JwtService.java` (creates/parses tokens)
  - `src/main/java/org/example/project/config/JwtFilter.java` (reads Authorization header and populates SecurityContext)
  - `src/main/java/org/example/project/config/SecurityConfig.java` (security rules, CORS, public endpoints)

Auth & permissions (critical)
- Tokens include a `permissions` claim: a list of authority strings (examples: `ROLE_ADMIN`, `PERMISSION_MANAGE_PRODUCTS`). See `JwtService.generateToken` for exact claim key.
- Permission enforcement is AOP-based: methods annotated with `@RequirePermission("PERMISSION_X")` are checked by `PermissionAspect` which reads authorities from the SecurityContext. Files: `valid/RequirePermission.java`, `valid/PermissionAspect.java`, `extra/Perms.java`.
- To add a new permission:
  1. Add a constant to `extra/Perms.java`.
 2. Use `@RequirePermission(Perms.YOUR_PERMISSION)` on controller/service methods.
 3. Ensure the JWT `permissions` claim contains the matching string for the user.

Conventions & patterns to follow
- Controllers return `ApiResponse` or domain types directly (see `extra/ApiResponse.java`). Follow existing method signatures and use DTOs for input validation (annotated with `@Valid`).
- URL pattern: controllers use `/api/v1/...` (see `@RequestMapping` on controllers).
- OpenAPI is included (`springdoc`) and exposed; SecurityConfig explicitly permits `/swagger-ui/**` and `/v3/api-docs/**`.

Build / run / test (examples for Windows PowerShell)
- Build package (uses wrapper included in repo):
```powershell
# from repository root
.\mvnw.cmd clean package -DskipTests=false
```
- Run tests only:
```powershell
.\mvnw.cmd test
```
- Run application from IDE or via wrapper:
```powershell
.\mvnw.cmd spring-boot:run
```
- Run packaged jar (after build):
```powershell
java -jar target\foodstore-0.0.1-SNAPSHOT.jar
```
- Overriding env/config at runtime (PowerShell examples):
```powershell
$env:DB_USERNAME = 'postgres'; $env:DB_PASSWORD = 'secret'; .\mvnw.cmd spring-boot:run
# or when running jar:
$env:DB_USERNAME='postgres'; $env:DB_PASSWORD='secret'; java -jar target\foodstore-0.0.1-SNAPSHOT.jar
```

Important runtime configuration to be aware of
- `src/main/resources/application.properties` contains defaults:
  - `spring.datasource.url=jdbc:postgresql://localhost:5432/foodstore`
  - DB credentials default to `${DB_USERNAME:postgres}` / `${DB_PASSWORD:123qwe&&}` — use environment variables in CI/prod
  - `spring.jpa.hibernate.ddl-auto=create` — database will be recreated on startup (dev-only behavior; be careful)
  - Mail defaults are present (Gmail example); do not commit real credentials — use `MAIL_USERNAME`/`MAIL_PASSWORD` env vars.
- CORS: allowed origin defaults to `http://localhost:3000` in `SecurityConfig.corsConfigurationSource()`.
- JWT secret can be overridden with `jwt.secret` property or env var. Default is in `JwtService` property placeholder.

Integration points & external dependencies
- PostgreSQL (driver present). Ensure DB is available when running integration tests or the app.
- SMTP for mailing (configured via Spring Mail properties) — tests use mail test starter; real sending needs credentials.
- OpenAPI UI is available for quick API inspection: `/swagger-ui.html` or `/swagger-ui/index.html`.

Files to inspect when implementing changes
- Security & auth: `config/SecurityConfig.java`, `config/JwtFilter.java`, `security/JwtService.java`, `valid/PermissionAspect.java`, `extra/Perms.java`.
- Common models: `dto/` and `entity/` folders.
- Controllers: `controller/` (many examples: `ProductController.java`, `OrderController.java`, `AuthController.java`). Copy patterns from existing controllers when adding new endpoints.

Notes for agents that change code
- Preserve existing exception types (e.g., `NotFoundException`, `ForbiddenException`) — `GlobalExceptionHandler` maps them to HTTP status codes.
- When adding new endpoints that require authorization, prefer adding `@RequirePermission` vs ad-hoc security checks.
- Maintain DTO validation annotations (`@Valid`) and return `ApiResponse` when appropriate to match API clients.

Where to run tests & CI
- Local: use the included Maven wrapper (`mvnw.cmd`). CI systems should set env vars for DB and mail and should NOT rely on `ddl-auto=create` for production data.

If you need more context
- Search for usages of `Perms`, `@RequirePermission`, `JwtService`, and `ApiResponse` for representative examples.

-- End of AGENTS.md

