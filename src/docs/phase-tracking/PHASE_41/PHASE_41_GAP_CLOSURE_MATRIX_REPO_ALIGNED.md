# Phase 41 — Repository-Aligned Gap Closure Matrix

> Status: Locked by ADR-041 repository-aligned revision

| # | Gap | Resolution | Artifact |
|---:|---|---|---|
| 1 | Module name | `modules/knowledge` | ADR §3 |
| 2 | Table prefix/API/tool | `knowledge_*`, `/api/knowledge`, `knowledge.search` | ADR §§1,4,14 |
| 3 | Flyway DDL | V95 exact Knowledge DDL | `V95__...sql` |
| 4 | Document migration wrong table | Uses `documenthub_version` | `V96__...sql` |
| 5 | Document columns wrong | Preserves `content_type`, `checksum`, `storage_key` | ADR §4 + V96 |
| 6 | Flyway placeholder versions | Locked V95/V96 from latest known V94 | ADR §4 |
| 7 | Package layout mismatch | Uses entity/http + application/action/service; no interfaces/rest | ADR §3 |
| 8 | ES mapping | 8.19.16, v001, 1536 cosine, aliases | Mapping JSON + ADR §12 |
| 9 | API request/response | Search/source/chunk/reindex/status/debug/graph exact JSON | API contract |
| 10 | Graph response missing | Exact node/edge response added | API §8 |
| 11 | Presigned API missing | Upload/complete/download contracts added under Document Hub | API §§9–10 |
| 12 | Error envelope mismatch | Existing ErrorResponse/AppException required | API §1 + ADR §14 |
| 13 | Chunking | deterministic chunk-v1 | ADR §7 |
| 14 | Source whitelist | TASK/DOCUMENT_VERSION/MEETING_MINUTE | ADR §5 |
| 15 | Embedding/model/dims | text-embedding-3-small, 1536, cosine | ADR §6 + V95 seed |
| 16 | ACL/signature | aclTokens + acl:v1 signature + source mapping | ADR §9 + V95 |
| 17 | Graph scope | 4 nodes/6 edges/depth/fan-out | ADR §10 |
| 18 | Reranker | optional/off; RRF mandatory | ADR §11 |
| 19 | IAM/Event artifacts | exact catalogs and initializer names | Seed catalog |
| 20 | Document extraction open | PDF/DOCX/TXT/MD; no OCR/XLSX/PPTX | ADR §8 + adapter contract |
| 21 | Source event mapping open | canonical task/document mapping + explicit meeting mapper | ADR §15 |
| 22 | Repo missing infra/deps | compose plus Maven dependency lock | compose + Maven artifact |
| 23 | Current-vs-TO-BE unknown | classified from supplied real repo state | Phase 41 matrix |
| 24 | Agent still able to invent | conflict/precedence and Definition of Ready locked | ADR §§2,19 |

Phase 41 may be scaffolded only after the Definition of Ready checks in ADR §19.
