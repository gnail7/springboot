---
name: demo-hub-conventions
description: >
  Use when creating or modifying Demo Hub catalog entries, anonymous demo sessions,
  SSE/WebSocket experiences, QR-code rooms, concurrency demos, Redis runtime state,
  or public demo APIs in this project.
---

# Demo Hub Conventions

## Public API boundary

- Public experience endpoints use `/demo-hub/**` and do not require login.
- Admin or maintenance endpoints remain under `/api/**` and use the existing JWT and permission rules.
- Use the existing `Result<T>` and `PageResult<T>` for ordinary JSON endpoints.
- Stream endpoints return `text/event-stream` or WebSocket frames directly; do not wrap stream frames in `Result<T>`.

## Generic model

Do not create one table per demo technology. Reuse these concepts:

```text
demo              catalog metadata and handler_key
demo_version      optional implementation version
demo_session      one runtime/room/stream experience
demo_participant  anonymous browser in a multi-user session
demo_event        optional low-volume history or replay data
```

Use `demo_type`, `interaction_mode`, `handler_key`, and `config_json` for type differences. Keep high-frequency runtime state in Redis and persist only the final result or events that must be replayed.

## Anonymous sessions

- Generate a random browser `anonymousId`; it is not an authenticated user identity.
- Generate unpredictable, short-lived session tokens for QR-code links.
- Enforce session TTL, participant limits, per-IP/device rate limits, and explicit `EXPIRED`/`FINISHED` states.
- Never trust a room code alone as authorization for administrative actions.

## SSE and concurrency

- Always register completion, timeout, and error cleanup for `SseEmitter` or WebSocket sessions.
- Bound event counts, intervals, room sizes, and request fan-out at the API boundary.
- Use Redis for countdowns, participant presence, Pub/Sub or Streams, and temporary idempotency keys.
- Do not write every heartbeat or client event to MySQL.
- For concurrency demos, record aggregate metrics such as total, success, failure, latency, and completion time in `demo_session.result_json`.

## Implementation shape

```text
DemoController       HTTP/SSE/WebSocket adapter
DemoService          session lifecycle and business rules
DemoHandler          demo-specific behavior selected by handler_key
DemoMapper           durable catalog/session persistence
Redis runtime        presence, TTL, event fan-out and rate limits
```

No arbitrary Java, SQL, shell, or container code may be accepted from public users for execution.
