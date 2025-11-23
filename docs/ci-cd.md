# CI/CD Baseline (November 2025)

## Source of Truth
- CI workflows live under `.github/workflows/ci.yaml`.
- CD workflow lives under `.github/workflows/cd.yaml`.
- Container build/publish logic references the root `Makefile`.
- Runtime manifests: `compose.yaml`, `compose.dev.yaml`, `compose.prod.yaml`.

## What Already Works
- **Frontend CI** runs on `push`/`PR` and executes `pnpm run build` + `pnpm run test` with Node 22.
- **Backend CI** builds via `./gradlew build` (unit tests + bootJar + JaCoCo HTML report generated locally under `backend/build/`).
- **CD** builds and pushes Docker images for frontend/backend (using Makefile targets) and triggers a remote redeploy over SSH (`make prod-up`), so production can be updated from `main`.
- **Artifacts for runtime**: Docker Hub is not used; images are pushed to `ghcr.io/fegor04/*`.
- **Monitoring stack** already versioned (`monitoring/` + Compose) and can be launched alongside apps.

## Improvements (November 2025)
- **CI quality gates** (`.github/workflows/ci.yaml`)
  - Frontend job now runs lint + `pnpm test:ci` (Vitest w/coverage + JUnit) + build, uploads coverage/dist artifacts, and posts test results via `dorny/test-reporter`.
  - Backend job runs `./gradlew clean test jacocoTestReport checkstyleMain checkstyleTest`, publishes JUnit + HTML reports + JaCoCo artifacts, and ships bootable JARs.
  - `frontend/vite.config.ts` enables `json/html/lcov` coverage reporters; `frontend/package.json` exposes a CI-friendly test command.
  - Checkstyle is configured via `config/checkstyle/checkstyle.xml` and wired into `backend/build.gradle.kts`.
- **Deployment automation** (`ansible/`)
  - Inventory (`inventory/prod.yml`), group vars, and a reusable `deploy` role install Docker, sync compose/manifests, authenticate to GHCR, pull tagged images, and run `docker compose up -d`.
  - Optional `.env` templating is available when `app_env` is provided (e.g., via secret JSON). Otherwise existing secrets remain untouched while Compose picks up new image tags via injected env vars.
- **CD workflow** (`.github/workflows/cd.yaml`)
  - Builds/pushes backend & frontend images tagged with `github.sha` by default.
  - Deploy job installs Ansible on the runner, renders `ansible/vars.auto.yml` (merging optional `PROD_APP_ENV_JSON` secret), and executes `ansible-playbook` to redeploy the stack deterministically.
- **Docs & artifacts**
  - Coverage/test assets are now downloadable from every CI run for transparency.
  - This document captures the current automation surface; update it whenever pipelines change.

To rotate secrets centrally, set a repository secret `PROD_APP_ENV_JSON` with JSON such as:

```json
{
  "POSTGRES_USER": "xxx",
  "POSTGRES_PASSWORD": "xxx",
  "POSTGRES_DB": "xxx",
  "JWT_SECRET": "xxx",
  "JWT_ACCESS_TTL": "15m",
  "JWT_REFRESH_TTL": "7d",
  "GIGACHAT_CLIENT_ID": "...",
  "GIGACHAT_AUTHORIZATION_KEY": "..."
}
```

The deploy job will merge that map into the environment passed to Docker Compose and (optionally) write a `.env` file on the remote host.

