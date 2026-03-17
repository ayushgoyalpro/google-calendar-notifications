# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

**Run the server:**
```sh
./mvnw spring-boot:run -f server/pom.xml
```

**Run the client:**
```sh
./mvnw spring-boot:run -f client/pom.xml
```

**Build client JAR (used for release):**
```sh
./mvnw package -DskipTests -f client/pom.xml
```

**Run tests:**
```sh
./mvnw test -f server/pom.xml
./mvnw test -f client/pom.xml
```

Java 21 and Maven are required. The wrapper (`./mvnw`) is included — no separate Maven install needed.

## Architecture

Three components work together:

1. **`google-app-scripts/Code.gs`** — runs every minute via Google Apps Script trigger. Fetches today's events from Google Calendar API v3 (`Calendar.Events.list`), MD5-hashes them for change detection, and POSTs the raw event list as `{ key, meetings[] }` to `/api/sync`.

2. **`server/`** — Spring Boot app (port 8080). Receives sync requests, rebuilds an in-memory alert schedule per user, and fires notifications at the right minute via a cron job.

3. **`client/`** — Spring Boot app (port 8081) running on the user's Mac. Connects to the server over STOMP WebSocket, subscribes to `/topic/<key>`, and triggers macOS popups via `osascript` when an alert arrives.

### Server internals

- **`AlertStore`** — on every `/api/sync`, discards the old schedule for that user key and rebuilds it fresh. For each meeting × each configured offset, computes `alertTime = meetingStart - offsetMinutes` and stores an `Alert` under its epoch-minute bucket. Past alert times are dropped at build time.
- **`AlertDispatcher`** — cron `0 * * * * *`. Gets all alerts for the current epoch-minute and fires each channel: STOMP broadcast for `WEBSOCKET`, HTTP POST to `ntfy.sh/<key>` for `NTFY`.
- **`UserPreferenceStore`** — in-memory map of user key → `UserPreferences`. Defaults: 15m→WEBSOCKET, 5m→WEBSOCKET, 0m→WEBSOCKET+NTFY. No persistence yet.
- **`UserSchedule`** — per-user `ConcurrentHashMap<Long, List<Alert>>` keyed by epoch-minute (`epochSeconds / 60`).

### Client internals

- **`ConnectionManager`** — connects via STOMP on `@PostConstruct`, auto-reconnects on transport/session errors using a single-threaded scheduled executor. The WebSocket URL and personal key come from `application.yaml` (`gcn-server.url`, `gcn-server.personal-key` via `${USER_KEY}` env var).
- **`MacNotificationHandler`** — builds and runs an `osascript` script. Shows a 3-line message (title, room info, offset). Buttons: `{"Dismiss", "Open Calendar", "Join Google Meet"}` (default: Join) when `hangoutLink` is present; `{"Dismiss", "Open Calendar"}` otherwise.

### DTO mirroring

`Meeting`, `Alert`, `AlertOffset`, and `Channel` are duplicated between `server/dto/` and `client/alert/`. They must stay in sync — the server serializes `Alert` over WebSocket and the client deserializes it. Both use `@JsonIgnoreProperties(ignoreUnknown = true)`.

### Key flow for a notification

```
Code.gs POST /api/sync
  → AlertStore.rebuildSchedule()         (stores alerts by epoch-minute)
  → AlertDispatcher cron tick            (every minute)
  → AlertDispatcher.fire(alert)
  → SimpMessagingTemplate → /topic/<key> (WEBSOCKET)
  → ConnectionManager.handleFrame()
  → MacNotificationHandler.handleAlert() (osascript popup)
```

## Configuration

**Client** (`client/src/main/resources/`):
- `application.yaml` — prod defaults; `gcn-server.url` points to Render deployment; `personal-key` reads `${USER_KEY}` env var.
- `application-local.yaml` — overrides URL to `ws://localhost:8080/ws-calendar` for local dev. Activate with `-Dspring.profiles.active=local`.

**Server** (`server/src/main/resources/application.yaml`): port 8080, actuator exposes `health`, `metrics`, `beans`.

## Testing endpoints

Bruno collection lives in `server/bruno/`. Use the `Local` environment (sets `host=http://localhost:8080`; `your_key` is a secret variable you set locally).

- **POST `/api/sync`** — send a `SyncRequest` (`key` + `meetings[]`) to rebuild a user's schedule.
- **POST `/tester/trigger`** — fires an alert immediately, bypassing the scheduler. Body: `{ key, channel, offset, meeting }`. Useful for testing `MacNotificationHandler` end-to-end without waiting for a cron tick.

## Releasing

Tag and push — GitHub Actions (`.github/workflows/release-client.yml`) builds the client JAR, creates a GitHub Release, and updates the Homebrew formula in `ayushgoyalpro/brew`:

```sh
git tag -a v1.2.3 -m "what changed"
git push origin v1.2.3
```