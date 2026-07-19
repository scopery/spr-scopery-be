# OpenAPI Review Notes — Phase 23

> Equivalent path: `src/docs/api/OPENAPI_REVIEW_NOTES.md`
> Status: **PARTIAL**

## Path / versioning

| Check | Result |
|---|---|
| Base path `/api` (not `/api/v1`) | **PASS** — `ApiPaths.BASE_PATH` |
| Deprecated version prefixes in controllers | **PASS (static regression)** — `SecurityPathPolicy.containsApiVersionPrefix`, project security regression |
| springdoc present | Yes — `springdoc-openapi-starter-webmvc-ui` |

## Contract quality gaps

1. Reporting dashboard / convenience / definition endpoints now use typed response records (Gate 6 remediating).
2. Export download returns raw bytes (`ResponseEntity<byte[]>`) — ensure OpenAPI documents content types (`text/csv`, `application/json`).
3. No published OpenAPI snapshot committed for drift detection.
4. Error catalog codes not fully reflected as documented response schemas.

## Follow-ups

- Commit `openapi.json` snapshot + CI diff (optional but recommended).
- Replace remaining Map responses with typed records before external clients freeze.
- Environment policy: protect `/swagger-ui` and `/v3/api-docs` in production.

## Decision

Gate 15 PASS. Broader HDN-009 cleanup remains PARTIAL.
