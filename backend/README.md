# Backend Auth

REST API для регистрации и авторизации мобильного приложения.

## Endpoints

- `GET /health`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`

## Register body

```json
{
  "email": "user@example.com",
  "name": "Alex",
  "password": "strongpass123"
}
```

## Login body

```json
{
  "email": "user@example.com",
  "password": "strongpass123"
}
```

## Run

1. Скопировать `.env.example` в `.env`
2. Запустить `docker compose up --build`
