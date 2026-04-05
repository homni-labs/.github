# homni-feature-toggle-backend

Feature toggle service with implementation substitution. Analogue of Unleash built on Quarkus.

## Tech Stack

| Layer          | Technology                         |
|----------------|------------------------------------|
| Runtime        | Java 21, Quarkus 3.17              |
| Database       | PostgreSQL 17                      |
| Migrations     | Liquibase                          |
| Auth (SSO)     | Authentik (OIDC)                   |
| Auth (API)     | API Key (`X-API-Key` header)       |
| API Contract   | OpenAPI 3.1 (`src/main/resources/openapi/api.yaml`) |

## Architecture

Hexagonal (Ports & Adapters) with strict DDD:

```
domain/
  model/         ← Aggregates, Value Objects, domain exceptions
  port/
    inbound/     ← Use case interfaces (driven side)
    outbound/    ← Repository interfaces (driving side)

application/     ← Thin orchestrators implementing inbound ports

infrastructure/
  adapter/
    inbound/rest/        ← JAX-RS resources, DTOs, mapper
    outbound/persistence/← Native SQL + JDBC adapters
  security/              ← API Key auth mechanism
  exception/             ← Global exception mapper
```

**Key constraints enforced:**
- No Hibernate — native parameterized SQL only
- No getters/setters — public final fields + records
- All business logic lives in domain aggregates
- Ports are never injected into domain objects
- Application services are thin orchestrators

## Quick Start

```bash
# Start PostgreSQL + Authentik
docker compose up -d postgres authentik-server authentik-worker

# Run in dev mode
./mvnw quarkus:dev

# Or build and run the full stack
docker compose up --build
```

The service starts on `http://localhost:8080`.

## API Endpoints

| Method | Path                                 | Description                       | Auth         |
|--------|--------------------------------------|-----------------------------------|--------------|
| GET    | `/api/v1/toggles`                    | List toggles                      | OIDC / Admin |
| POST   | `/api/v1/toggles`                    | Create toggle                     | OIDC / Admin |
| GET    | `/api/v1/toggles/{id}`               | Get toggle                        | OIDC / Admin |
| DELETE | `/api/v1/toggles/{id}`               | Delete toggle                     | OIDC / Admin |
| POST   | `/api/v1/toggles/{id}/enable`        | Enable toggle                     | OIDC / Admin |
| POST   | `/api/v1/toggles/{id}/disable`       | Disable toggle                    | OIDC / Admin |
| GET    | `/api/v1/toggles/{id}/rules`         | List implementation rules         | OIDC / Admin |
| POST   | `/api/v1/toggles/{id}/rules`         | Add implementation rule           | OIDC / Admin |
| DELETE | `/api/v1/toggles/{id}/rules/{ruleId}`| Remove implementation rule        | OIDC / Admin |
| POST   | `/api/v1/evaluate`                   | Evaluate toggles against context  | OIDC / API Key |
| GET    | `/api/v1/api-keys`                   | List API keys                     | OIDC / Admin |
| POST   | `/api/v1/api-keys`                   | Issue API key                     | OIDC / Admin |
| DELETE | `/api/v1/api-keys/{id}`              | Revoke API key                    | OIDC / Admin |

**Swagger UI:** `http://localhost:8080/q/swagger-ui`

## Evaluation Example

```bash
curl -X POST http://localhost:8080/api/v1/evaluate \
  -H "X-API-Key: hft_your_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "projectKey": "homni-web",
    "toggleNames": ["dark-mode", "payment-processor"],
    "context": {
      "region": "EU",
      "userId": "user-42"
    }
  }'
```

Response:
```json
{
  "results": [
    { "toggleName": "dark-mode",          "enabled": true,  "implementation": "dark-theme" },
    { "toggleName": "payment-processor",  "enabled": true,  "implementation": "stripe" }
  ]
}
```

## Configuration

All settings are overridden via environment variables:

| Variable               | Default                                   | Description                |
|------------------------|-------------------------------------------|----------------------------|
| `DB_HOST`              | `localhost`                               | PostgreSQL host            |
| `DB_PORT`              | `5432`                                    | PostgreSQL port            |
| `DB_NAME`              | `homni_feature_toggle`                    | Database name              |
| `DB_USER`              | `homni`                                   | Database user              |
| `DB_PASSWORD`          | `homni`                                   | Database password          |
| `AUTHENTIK_URL`        | `https://authentik.example.com/...`       | Authentik OIDC endpoint    |
| `AUTHENTIK_CLIENT_ID`  | `homni-feature-toggle`                    | OIDC client ID             |
| `AUTHENTIK_CLIENT_SECRET`| `change-me`                             | OIDC client secret         |

## Database Schema

Managed automatically by Liquibase at startup. Three tables:

- **`feature_toggle`** — core toggle with `(project_key, name)` uniqueness
- **`implementation_rule`** — context-based rules, FK to `feature_toggle` with `ON DELETE CASCADE`
- **`api_key`** — API keys with SHA-256 hashed tokens, partial index on active keys
