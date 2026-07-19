# Phase 29 — External Party / Client / Stakeholder Complete

## 1. Summary

Phase 29 delivered `modules/externalparty`: ProjectStakeholder create/list/get, ExternalOrganization + ExternalContact workspace CRUD, ProjectExternalPartyRelationship, ProjectApprovalAuthority, Flyway `V66` (+ `V68` audit alignment), event seeder `@Order(35)`, IAM `EXTERNAL_PARTY_*`.

## 2. Source Inputs Reviewed

- `PHASE_29_EXTERNAL_PARTY_CLIENT_STAKEHOLDER_TO_BE_DETAILED.md`

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| ProjectStakeholder APIs | MUST | Implemented |
| ExternalOrganization / Contact HTTP | MUST | Implemented |
| Project relationship / ApprovalAuthority | MUST | Implemented |
| Events + IAM | MUST | Implemented |

## 4. Implemented in Current BE

- `V66__create_external_party_tables_phase29.sql`
- Controllers: stakeholders, external-organizations, external-contacts, external-relationships, approval-authorities
- Unit test: ProjectStakeholderDomainTest

## 5. Deferred Items

None for Phase 29 required MUST scope.

| Item | Notes |
|---|---|
| Privacy masking suite | Cross-cutting hardening; not required for CRM HTTP loop |

## 6. Release Decision

**Phase 29 MUST path: COMPLETE** — org/contact CRM, project relationships, approval authorities, stakeholders.
