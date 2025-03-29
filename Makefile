include .env

FRONTEND_IMAGE_NAME ?= sber-frontend
BACKEND_IMAGE_NAME ?= sber-backend


FRONTEND_VERSION ?= 0.1.0
BACKEND_VERSION ?= 0.1.1


FRONTEND_IMAGE = ghcr.io/fegor04/${FRONTEND_IMAGE_NAME}:${FRONTEND_VERSION}
BACKEND_IMAGE = ghcr.io/fegor04/${BACKEND_IMAGE_NAME}:${BACKEND_VERSION}

POSTGRES_IMAGE=postgres:17.2
TRAEFIK_IMAGE=traefik:3.2

export

all: build-backend build-frontend

build-backend: backend
	cd backend && ./gradlew bootBuildImage --imageName=${BACKEND_IMAGE}

build-frontend: frontend
	cd frontend && docker build . -t ${FRONTEND_IMAGE}

dev-up:
	docker compose -f compose.yaml -f compose.dev.yaml up -d

dev-down:
	docker compose -f compose.yaml -f compose.dev.yaml down

dev-up-postgres:
	docker compose -f compose.yaml -f compose.dev.yaml up postgres -d

dev-up-backend:
	docker compose -f compose.yaml -f compose.dev.yaml up postgres backend -d

push-backend:
	docker push ${BACKEND_IMAGE}

push-frontend:
	docker push ${FRONTEND_IMAGE}

prod-up:
	docker compose -f compose.yaml -f compose.prod.yaml up -d

prod-down:
	docker compose -f compose.yaml -f compose.prod.yaml down

.env: .env.example
	cp .env.example .env

