# DeviceLK Service Discovery

Production-ready Eureka discovery server for DeviceLK microservices.

## Quick Start (Development)

1. Run locally with default dev profile:

```bash
mvn spring-boot:run
```

2. Open endpoints:
- Eureka UI: http://localhost:8761/
- Health: http://localhost:8761/actuator/health
- Readiness: http://localhost:8761/actuator/health/readiness

Default credentials (dev/local):
- Username: `discovery`
- Password: `changeit`

## Production Profile

Run with explicit prod profile and peer URL list:

```bash
SPRING_PROFILES_ACTIVE=prod \
DISCOVERY_ADMIN_USER=discovery \
DISCOVERY_ADMIN_PASSWORD=changeit \
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE='http://discovery:changeit@eureka-1:8761/eureka/,http://discovery:changeit@eureka-2:8761/eureka/' \
mvn spring-boot:run
```

Fail-fast behavior:
- Startup fails in `prod` if `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` is missing.

## Environment Variables

- `SERVER_PORT` (default `8761`)
- `SPRING_PROFILES_ACTIVE` (`dev` default, `prod` for HA)
- `DISCOVERY_ADMIN_USER` (default `discovery`)
- `DISCOVERY_ADMIN_PASSWORD` (default `changeit`)
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` (required in prod)
- `SERVICE_NAME` (default `devicelk-discovery-server`)
- `SERVICE_ENV` (default `dev`)
- `SERVICE_VERSION` (default `local`)
- `INSTANCE_ZONE` (default `local`)
- `SERVICE_TEAM` (default `platform`)
- `GIT_COMMIT` (default `unknown`)

## Security Model

- Public:
  - `/actuator/health/**`
  - `/actuator/info`
- Auth required:
  - `/eureka/**`
  - `/` (Eureka dashboard)
  - `/actuator/prometheus`
  - any other actuator endpoints

## Observability

- Metrics endpoint: `/actuator/prometheus`
- Health groups:
  - `/actuator/health/liveness`
  - `/actuator/health/readiness`
- Logs:
  - `dev` profile uses readable console pattern.
  - `prod` profile emits JSON logs.

## High Availability

### Local HA with Docker Compose

```bash
docker compose up --build
```

Creates 2 Eureka peers:
- http://localhost:8761/
- http://localhost:8762/

### Kubernetes

Apply manifests:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/statefulset.yaml
```

## Client Registration Contract

See:
- `.docs/registration-rules.md`
- `.docs/client-resilience-guide.md`
- `examples/sample-client`

## Tests

Run tests:

```bash
mvn test
```

Coverage focus:
- startup checks
- profile loading
- env validation
- secured registry access

## Troubleshooting

- `401 Unauthorized` on Eureka UI: verify admin user/password env vars.
- Startup fails in prod: check `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`.
- Peer sync issues: confirm both peer URLs are reachable and include credentials.
- No metrics scrape: verify authenticated access to `/actuator/prometheus`.
