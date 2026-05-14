# Anxiety Backend

Backend for the mobile anxiety-tracking app.

Stack:
- Go
- REST API
- PostgreSQL
- Docker

## What Is Implemented

- User registration
- User login
- JWT authentication
- Thought sync between devices
- Thought statistics for week, month, and year
- Support message selection based on today's average anxiety level
- Notification frequency settings storage

## Project Structure

- `main.go` - application entrypoint
- `internal/auth` - JWT logic
- `internal/users` - registration and login
- `internal/thoughts` - thought sync and statistics
- `internal/notifications` - support messages and notification settings
- `migrations` - SQL schema files
- `docker-compose.yml` - local infrastructure

## Environment Variables

Create `.env` in the `backend` folder.

Example:

```env
APP_PORT=8080
DATABASE_URL=postgres://postgres:postgres@db:5432/anxiety_app?sslmode=disable
JWT_SECRET=change-me-to-a-long-random-secret
JWT_ISSUER=anxiety-app
JWT_TOKEN_TTL_MINUTES=10080
```

## Run With Docker

From the `backend` directory:

```powershell
docker compose up --build
```

API will be available at:

```text
http://localhost:8080
```

PostgreSQL will be available at:

```text
localhost:5433
```

## Rebuild Database From Scratch

Use this when you add new migrations and want a clean local database:

```powershell
docker compose down -v
docker compose up --build
```

Warning:
- `down -v` removes local PostgreSQL data

## Run Without Docker

Requirements:
- Go installed
- PostgreSQL installed and running
- database `anxiety_app` created
- SQL migrations applied manually

Then run:

```powershell
go run main.go
```

## Authentication Flow

1. Mobile app calls `POST /api/v1/auth/register` or `POST /api/v1/auth/login`
2. Backend returns JWT token
3. Mobile app stores token
4. Mobile app sends token in protected requests:

```http
Authorization: Bearer <token>
```

## API Endpoints

### Health

`GET /health`

Response:

```json
{
  "status": "ok"
}
```

### Register

`POST /api/v1/auth/register`

Body:

```json
{
  "email": "user@example.com",
  "name": "Alex",
  "password": "strongpass123"
}
```

### Login

`POST /api/v1/auth/login`

Body:

```json
{
  "email": "user@example.com",
  "password": "strongpass123"
}
```

### Current User

`GET /api/v1/auth/me`

Header:

```http
Authorization: Bearer <token>
```

## Thoughts

### Sync Thoughts

`POST /api/v1/thoughts/sync`

Header:

```http
Authorization: Bearer <token>
```

Body:

```json
{
  "entries": [
    {
      "anxiety_level": 8,
      "anxiety_type": "health",
      "description": "I am worried that something is wrong with my heart."
    },
    {
      "anxiety_level": 5,
      "anxiety_type": "work",
      "description": "I am afraid I will fail an important task."
    }
  ]
}
```

Behavior:
- mobile app sends locally saved entries when internet becomes available
- backend saves new entries for the current user
- backend returns all user thoughts after sync

### Get All Thoughts

`GET /api/v1/thoughts`

Header:

```http
Authorization: Bearer <token>
```

## Statistics

### Get Thought Statistics

`GET /api/v1/thoughts/statistics`

Header:

```http
Authorization: Bearer <token>
```

Response contains:
- `week`
- `month`
- `year`

Each period contains:
- `entries_count`
- `average_anxiety_level`
- `daily_dynamics`
- `anxiety_type_frequencies`
- `most_anxious_time`

Notes:
- week = last 7 days
- month = last 30 days
- year = last 365 days
- `daily_dynamics` contains only days where entries exist
- time buckets:
  - `night` = `00:00-05:59`
  - `morning` = `06:00-11:59`
  - `day` = `12:00-17:59`
  - `evening` = `18:00-23:59`

Example:

```json
{
  "week": {
    "period": "week",
    "from": "2026-05-06T00:00:00Z",
    "to": "2026-05-12T14:00:00Z",
    "entries_count": 4,
    "average_anxiety_level": 6.75,
    "daily_dynamics": [
      {
        "date": "2026-05-10",
        "average_anxiety_level": 5.5,
        "entries_count": 2
      },
      {
        "date": "2026-05-12",
        "average_anxiety_level": 8,
        "entries_count": 2
      }
    ],
    "anxiety_type_frequencies": [
      {
        "anxiety_type": "health",
        "count": 3
      },
      {
        "anxiety_type": "work",
        "count": 1
      }
    ],
    "most_anxious_time": "evening"
  }
}
```

## Notification Support Messages

Current implementation:
- backend stores notification frequency as part of user settings
- backend does not send remote push notifications by itself
- mobile app can request a support message and show a local notification

### Save Notification Settings

`POST /api/v1/notifications/settings`

Header:

```http
Authorization: Bearer <token>
```

Body:

```json
{
  "frequency_minutes": 180
}
```

### Get Notification Settings

`GET /api/v1/notifications/settings`

Header:

```http
Authorization: Bearer <token>
```

### Get Support Message

`GET /api/v1/notifications/support-message`

Header:

```http
Authorization: Bearer <token>
```

Response example:

```json
{
  "frequency_minutes": 180,
  "average_daily_anxiety": 5.5,
  "anxiety_range": "medium",
  "message": "Сейчас тебе может быть тяжело, но это состояние пройдёт.",
  "generated_at": "2026-05-12T15:00:00Z"
}
```

How it works:
- backend calculates today's average anxiety for the user
- backend selects one of three phrase groups:
  - `low` for `0-3`
  - `medium` for `4-6`
  - `high` for `7-10`
- backend returns one random support phrase from the selected group

## PostgreSQL Tables

- `users`
- `anxiety_thoughts`
- `user_notification_settings`

## Migrations

- `001_create_users.sql`
- `002_create_anxiety_thoughts.sql`
- `003_create_user_notification_settings.sql`

Important:
- Docker auto-runs migrations only for a fresh database volume
- if the database already exists, either recreate the volume or apply new SQL manually

## Mobile Development Notes

### Android Emulator

Use:

```text
http://10.0.2.2:8080
```

Not:

```text
http://localhost:8080
```

### Real Device In Local Network

Use host machine local IP, for example:

```text
http://192.168.1.25:8080
```

### Radmin VPN Scenario

If backend runs on one machine and another developer/device accesses it over Radmin VPN:

```text
http://<radmin_vpn_ip>:8080
```

## Current Limitations

- thought sync is currently append-only
- notification support messages are fetched by the mobile app, not pushed by backend
- true remote push delivery would require device tokens and FCM integration
- statistics are currently calculated in UTC

## Suggested Next Improvements

- refresh token flow
- proper sync with client-side entry IDs and upsert logic
- device token storage
- Firebase Cloud Messaging integration
- timezone-aware statistics
- OpenAPI / Swagger documentation
