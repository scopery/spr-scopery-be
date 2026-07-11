# PHASE 33 — TO-BE Custom Fields, Dynamic Forms, Object Configuration, Taxonomy, Tags & Workspace Configuration Layer

> Project: Scopery Backend  
> Phase: 33  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 28 — Application / Requirement / Traceability, Phase 29 — External Party / Stakeholder, Phase 30 — External Portal, Phase 31 — Meetings / Collaboration, Phase 32 — Search / Navigation / Productivity  
> API base: `/api`  
> Primary module: `modules/configuration`, `modules/customfield`, `modules/forms`, or `modules/platformconfig` depending on repository architecture  
> Related modules: `workspace`, `iam`, `project`, `task`, `document`, `requirements`, `quality`, `externalparty`, `clientportal`, `workflow`, `reporting`, `search`, `audit`, `eventregistry`, future `workflow`, `automation`, `semantic-index`, `integration`  
> Important rule: Phase 33 introduces safe workspace-level configuration, custom fields, dynamic forms, taxonomy, labels, statuses, and layout metadata. It does **not** implement a no-code app builder, arbitrary database schema changes, dynamic code execution, workflow automation, custom scripting, or frontend UI builder.

---

# 0. Purpose

Phase 33 gives Scopery a safe configuration layer so different teams can adapt the platform without changing backend code.

Earlier phases created many fixed objects:

```text
Project
Task
Deliverable
Requirement
Defect
Release
Document
RAID Item
Decision
ExternalContact
Meeting
ClientFeedback
```

But real Work OS usage needs flexibility:

```text
custom fields per object
custom forms for data entry
custom labels and categories
workspace-specific status sets
controlled tags
priority/severity taxonomies
custom validation rules
display layout metadata
field-level visibility
client-visible field control
search/reporting support for custom fields
```

Phase 33 answers:

```text
How can a workspace add fields to Tasks or Requirements safely?
How can a team define a custom intake form?
How can a project type have its own fields?
How can statuses/categories be configured without breaking domain rules?
How are custom fields validated?
How do custom fields appear in search, reporting, saved views, and exports?
How do we prevent custom fields from leaking sensitive data?
How do we avoid dangerous dynamic schema/code execution?
```

Phase 33 is the **safe configurability layer**.

---

# 1. Source inputs

Before coding Phase 33, the agent must read:

```text
1. Current backend codebase
2. Phase 01 API/Security baseline implementation
3. Phase 02 IAM implementation
4. Phase 03 Workspace/Organization/Team implementation
5. Phase 04 Audit/Outbox/Idempotency implementation
6. Phase 09 Project/Task implementation
7. Phase 10 Project Authorization implementation
8. Phase 22 Reporting implementation
9. Phase 23 Core Hardening completion file
10. Phase 24 Scope/Deliverable/Acceptance implementation
11. Phase 25 RAID/Decision implementation
12. Phase 26 Quality/Test/Release implementation
13. Phase 27 Document Hub implementation
14. Phase 28 Requirement/Traceability implementation
15. Phase 29 External Party implementation
16. Phase 30 External Portal implementation
17. Phase 31 Collaboration implementation
18. Phase 32 Search/Productivity implementation
19. Current enum/status/category/custom field code if any
20. Current validation/configuration code if any
21. Current IAM seeders and EventDefinition seeders
```

The coding agent must inspect real code and not assume custom-field support exists.

---

# 2. Current expected backend state

After Phase 32, Scopery likely has many hard-coded fields/statuses:

```text
Task priority/status
Requirement type/status/priority
Defect severity/status/category
Document type/confidentiality
RAID type/status/severity
Meeting type/status
ExternalContact roles
Project type/status
```

Likely missing:

```text
Configurable ObjectType metadata
CustomFieldDefinition
CustomFieldOption
CustomFieldValue
CustomFieldValidationRule
CustomFormDefinition
CustomFormSection
CustomFormField
CustomLayoutDefinition
TagDefinition
TagAssignment
TaxonomyTerm
ConfigSet
StatusSet
CategorySet
FieldVisibilityPolicy
CustomFieldSearchIndex
CustomFieldReport integration
Config versioning / publish lifecycle
```

Phase 33 implements these foundations.

---

# 3. Target statement

Phase 33 must deliver:

```text
1. Workspace-level configuration registry.
2. Object type metadata for configurable entities.
3. Custom field definitions with safe supported types.
4. Custom field values attached to supported target objects.
5. Field option lists, validation rules, and visibility rules.
6. Dynamic form definitions using allowed fields.
7. Form sections and field layout metadata.
8. Tags and taxonomy terms.
9. Configurable category/status/priority sets where safe.
10. Field-level client visibility and sensitivity controls.
11. Integration with search, saved views, reporting, export, portal, and audit.
12. Configuration lifecycle: draft, active, archived.
13. Versioning / publish strategy for forms and config sets.
14. Tests and completion report.
```

---

# 4. Boundary decisions

## 4.1 Custom fields do not change database schema

Phase 33 must not run arbitrary migrations per workspace.

Recommended storage:

```text
custom_field_definition
custom_field_value
JSONB value fields
typed value columns for indexing when needed
```

No dynamic DDL per user.

## 4.2 Custom fields do not override core domain rules

Example:

```text
A custom Task status cannot bypass task lifecycle rules.
A custom required field cannot make invalid domain object valid.
A custom field cannot override IAM.
```

Custom config extends data; it does not break core.

## 4.3 Dynamic forms are not frontend UI builder

Backend can store form schema/layout metadata.

Frontend rendering is separate.

No drag-and-drop builder engine is implemented in backend.

## 4.4 No custom scripting

Phase 33 must not allow arbitrary JS/Groovy/Python/SQL execution.

Validation rules must be declarative.

## 4.5 Workflow automation is deferred

Custom forms/fields can later feed workflow conditions.

Workflow execution is Phase 34.

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_33` | Required now. |
| `SEED_ONLY_IN_PHASE_33` | Seed metadata/default config only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later Work OS backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_33` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 CFG-001 — ConfigurableObjectType

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Declare which Scopery objects can support custom fields/forms/tags.
```

Supported initial object types:

```text
PROJECT
TASK
DELIVERABLE
REQUIREMENT
DEFECT
RELEASE_PACKAGE
DOCUMENT
RAID_ITEM
DECISION
MEETING
MEETING_ACTION_ITEM
EXTERNAL_ORGANIZATION
EXTERNAL_CONTACT
CLIENT_FEEDBACK
CHANGE_REQUEST
QUOTE_VERSION
```

Rules:

```text
1. ObjectType code stable and seeded.
2. ObjectType maps to existing domain entity.
3. ObjectType controls which custom features are allowed.
4. ObjectType does not create new domain entity.
5. ObjectType availability is permission-aware.
```

Feature flags per object type:

```text
customFieldsEnabled
formsEnabled
tagsEnabled
customStatusEnabled
clientVisibleFieldsEnabled
reportableFieldsEnabled
searchableFieldsEnabled
```

---

## 6.2 CF-001 — CustomFieldDefinition

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Define a custom field for a supported object type.
```

Field types:

```text
TEXT
LONG_TEXT
NUMBER
DECIMAL
CURRENCY
DATE
DATETIME
BOOLEAN
SELECT
MULTI_SELECT
USER
TEAM
EXTERNAL_CONTACT
EXTERNAL_ORGANIZATION
PROJECT
TASK
DOCUMENT
URL
EMAIL
PHONE
PERCENTAGE
FORMULA_READONLY future/deferred
JSON future/restricted
```

Rules:

```text
1. Field belongs to workspace.
2. Field targets one ObjectType.
3. Field key unique per workspace + objectType.
4. Label required.
5. Type immutable after values exist unless migration strategy exists.
6. Required flag supported.
7. Sensitive flag supported.
8. Client-visible flag supported if object supports it.
9. Searchable/reportable flags controlled by type and permission.
10. Field definition archived, not hard deleted if values exist.
```

---

## 6.3 CF-002 — CustomFieldOption

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Define options for SELECT/MULTI_SELECT fields.
```

Rules:

```text
1. Option belongs to CustomFieldDefinition.
2. Option value/code unique within field.
3. Option order supported.
4. Option can be archived.
5. Archived option remains valid for historical values but unavailable for new entries.
```

---

## 6.4 CF-003 — CustomFieldValue

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Store a value for a custom field on a target object.
```

Rules:

```text
1. Value belongs to field definition.
2. Target object must match field objectType.
3. Actor must have update permission for target object.
4. Value type validated.
5. Required fields enforced on configured forms or object save policy.
6. Sensitive values masked unless permission.
7. Client-visible custom values only exposed through portal when allowed.
8. Historical value changes audited.
```

Storage recommended:

```text
value_text
value_long_text
value_number
value_decimal
value_boolean
value_date
value_datetime
value_json
option_ids
target_reference_type
target_reference_id
```

---

## 6.5 CF-004 — CustomFieldValidationRule

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Attach declarative validation rules to custom fields.
```

Supported rules:

```text
MIN_LENGTH
MAX_LENGTH
MIN_VALUE
MAX_VALUE
REGEX
REQUIRED
UNIQUE_WITHIN_WORKSPACE
UNIQUE_WITHIN_PROJECT
ALLOWED_DOMAIN
DATE_AFTER
DATE_BEFORE
OPTION_REQUIRED
MAX_OPTIONS
```

Rules:

```text
1. Declarative only.
2. No arbitrary code.
3. Regex length/complexity limited.
4. Validation errors structured.
5. Rules versioned or audited when changed.
```

---

## 6.6 CF-005 — FieldVisibilityPolicy

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Control visibility/editability of custom fields.
```

Visibility dimensions:

```text
internalVisible
clientVisible
portalVisible
reportVisible
searchVisible
exportVisible
editableByRoles
readableByRoles
```

Rules:

```text
1. Sensitive fields default not clientVisible.
2. Field visibility does not grant target object access.
3. Portal visibility requires Phase 30 grant and artifact visibility.
4. Export visibility checked separately.
5. Masking applied in search/report/inbox/export.
```

---

## 6.7 FORM-001 — CustomFormDefinition

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Define a form for creating/updating/reviewing an object.
```

Form types:

```text
CREATE
EDIT
INTAKE
REVIEW
APPROVAL_INPUT
PORTAL_SUBMISSION
UAT_FEEDBACK
CLIENT_FEEDBACK
CUSTOM
```

Rules:

```text
1. Form belongs to workspace and ObjectType.
2. Form has version/lifecycle.
3. Form can be project-specific or workspace-wide.
4. Form can include core fields and custom fields.
5. Form does not bypass domain validation.
6. Published form version immutable.
7. Form submission can create/update target object only through domain services.
```

---

## 6.8 FORM-002 — CustomFormSection

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Group fields in form.
```

Rules:

```text
1. Section belongs to FormVersion.
2. Title required.
3. Sort order required.
4. Section visibility can be configured declaratively.
5. Client-visible section must not include internal-only fields.
```

---

## 6.9 FORM-003 — CustomFormField

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Place fields in form with layout and behavior metadata.
```

Field sources:

```text
CORE_FIELD
CUSTOM_FIELD
READONLY_DISPLAY
INSTRUCTION_TEXT
SEPARATOR
```

Rules:

```text
1. Custom field must target same ObjectType.
2. Required-on-form can be stricter than field definition.
3. Hidden field not submitted by client unless policy allows.
4. Readonly field cannot be modified through form.
5. Client portal form cannot include internal-only fields.
```

---

## 6.10 FORM-004 — FormSubmission

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Record submission payload and validation result for dynamic forms.
```

Rules:

```text
1. Submission belongs to FormVersion.
2. Actor and principal type recorded.
3. Validation result stored.
4. Successful submission calls domain action.
5. Failed validation does not mutate target.
6. Sensitive values masked in logs.
7. Portal submissions audited.
```

---

## 6.11 TAG-001 — TagDefinition

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Define controlled tags for workspace/project objects.
```

Rules:

```text
1. Tag belongs to workspace.
2. Tag name/code unique within workspace.
3. Tag can have color/description.
4. Tag can be restricted to object types.
5. Tag can be archived.
6. Tag does not grant access.
```

---

## 6.12 TAG-002 — TagAssignment

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Assign tags to objects.
```

Rules:

```text
1. Target object must exist and be visible/editable.
2. Tag object type restriction enforced.
3. Duplicate active tag assignment prevented.
4. Tag assignment does not grant access.
5. Tag assignment audited for sensitive objects.
```

---

## 6.13 TAX-001 — TaxonomyTerm

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Create controlled hierarchical vocabularies.
```

Taxonomy examples:

```text
Industry
Project Category
Technology Stack
Risk Category
Requirement Domain
Document Category
Client Segment
Defect Category
```

Rules:

```text
1. Taxonomy belongs to workspace.
2. Term can be hierarchical.
3. Term code unique under taxonomy.
4. Terms can be archived.
5. Term usage does not grant access.
```

---

## 6.14 STAT-001 — StatusSet foundation

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Allow safe custom status labels/categories where domain permits.
```

Important boundary:

```text
Core domain lifecycle remains authoritative.
```

Example:

```text
Task core status = IN_PROGRESS.
Workspace display status = "Waiting on Client" mapped to IN_PROGRESS/BLOCKED category.
```

Rules:

```text
1. StatusSet belongs to workspace/objectType.
2. Status maps to allowed domain status category.
3. Cannot create status that breaks lifecycle.
4. Cannot skip required domain transitions.
5. Custom status change still uses domain service.
```

---

## 6.15 CAT-001 — Category / Priority / Severity sets

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Configure labels for category/priority/severity where safe.
```

Rules:

```text
1. Set belongs to workspace/objectType.
2. Values mapped to normalized severity/priority weight when needed.
3. Reporting uses normalized weight plus label.
4. Archiving preserves historical values.
```

---

## 6.16 LAY-001 — LayoutDefinition

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Purpose:

```text
Store display layout metadata for object detail/list forms.
```

Layouts:

```text
DETAIL
CREATE_FORM
EDIT_FORM
PORTAL_DETAIL
LIST_COLUMNS
BOARD_CARD
```

Rules:

```text
1. Layout belongs to workspace/objectType.
2. Layout references allowed core/custom fields.
3. Layout does not grant field access.
4. Layout not a frontend builder; only metadata.
5. Invalid field references rejected.
```

---

## 6.17 CFG-002 — Config lifecycle and publishing

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Lifecycle:

```text
DRAFT
PUBLISHED
ARCHIVED
```

For:

```text
forms
layouts
status sets
taxonomy sets
```

Rules:

```text
1. Draft editable.
2. Published immutable except new version.
3. Archived hidden from new usage.
4. Current/published version unique per scope if required.
5. Version changes audited.
```

---

## 6.18 SRCH-001 — Search integration

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Rules:

```text
1. Searchable custom fields can be indexed.
2. Sensitive custom fields excluded or restricted.
3. Search snippets do not leak hidden custom values.
4. Custom field search filters supported for selected types.
5. Search re-checks target access.
```

---

## 6.19 RPT-001 — Reporting / export integration

Classification: `MUST_IMPLEMENT_IN_PHASE_33`

Rules:

```text
1. Reportable custom fields can be selected in reports.
2. Exportable custom fields controlled by visibility policy.
3. Sensitive fields masked or omitted.
4. Saved views can include custom fields.
5. Portal exports only include portal-visible fields.
```

---

## 6.20 AI-001 — AI-assisted configuration suggestions

Classification: `SEED_ONLY_IN_PHASE_33` or `MUST_IMPLEMENT_IN_PHASE_33` if Phase 21 tool registry exists.

AI can suggest:

```text
custom fields for a project type
form layout from requirements
tag taxonomy
status mapping
field validation rules
portal-safe field visibility
```

Rules:

```text
1. AI output is proposal only.
2. Human must approve/publish config.
3. AI cannot create custom scripts.
4. AI cannot expose sensitive fields to portal.
5. AI cannot bypass config validation.
```

---

# 7. Entity model TO-BE

---

## 7.1 ConfigurableObjectType — `config_object_type`

Fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
domain_entity VARCHAR(255) NULL
custom_fields_enabled BOOLEAN NOT NULL DEFAULT false
forms_enabled BOOLEAN NOT NULL DEFAULT false
tags_enabled BOOLEAN NOT NULL DEFAULT false
custom_status_enabled BOOLEAN NOT NULL DEFAULT false
client_visible_fields_enabled BOOLEAN NOT NULL DEFAULT false
reportable_fields_enabled BOOLEAN NOT NULL DEFAULT false
searchable_fields_enabled BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
created_at / updated_at
version INT
```

Constraint:

```text
unique code
```

---

## 7.2 CustomFieldDefinition — `config_custom_field_definition`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
object_type_code VARCHAR(100) NOT NULL
field_key VARCHAR(150) NOT NULL
label VARCHAR(255) NOT NULL
description TEXT NULL
field_type VARCHAR(50) NOT NULL
required BOOLEAN NOT NULL DEFAULT false
sensitive BOOLEAN NOT NULL DEFAULT false
client_visible BOOLEAN NOT NULL DEFAULT false
searchable BOOLEAN NOT NULL DEFAULT false
reportable BOOLEAN NOT NULL DEFAULT false
exportable BOOLEAN NOT NULL DEFAULT false
default_value_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + object_type_code + field_key
```

---

## 7.3 CustomFieldOption — `config_custom_field_option`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
custom_field_definition_id UUID NOT NULL
option_code VARCHAR(150) NOT NULL
label VARCHAR(255) NOT NULL
description TEXT NULL
color VARCHAR(50) NULL
sort_order INT NOT NULL DEFAULT 0
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique custom_field_definition_id + option_code
```

---

## 7.4 CustomFieldValue — `config_custom_field_value`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
custom_field_definition_id UUID NOT NULL
value_text TEXT NULL
value_long_text TEXT NULL
value_number BIGINT NULL
value_decimal DECIMAL(30,8) NULL
value_boolean BOOLEAN NULL
value_date DATE NULL
value_datetime TIMESTAMP NULL
value_json JSONB NULL
value_option_ids JSONB NULL
target_reference_type VARCHAR(100) NULL
target_reference_id UUID NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique custom_field_definition_id + target_id
```

Important:

```text
Target object validation must include object_type_code.
```

---

## 7.5 CustomFieldValidationRule — `config_custom_field_validation_rule`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
custom_field_definition_id UUID NOT NULL
rule_type VARCHAR(100) NOT NULL
rule_config_json JSONB NOT NULL
error_message VARCHAR(500) NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.6 FieldVisibilityPolicy — `config_field_visibility_policy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
custom_field_definition_id UUID NOT NULL
internal_visible BOOLEAN NOT NULL DEFAULT true
client_visible BOOLEAN NOT NULL DEFAULT false
portal_visible BOOLEAN NOT NULL DEFAULT false
report_visible BOOLEAN NOT NULL DEFAULT false
search_visible BOOLEAN NOT NULL DEFAULT false
export_visible BOOLEAN NOT NULL DEFAULT false
readable_roles_json JSONB NULL
editable_roles_json JSONB NULL
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.7 CustomFormDefinition — `config_custom_form_definition`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
form_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
form_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
current_version_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + project_id + object_type_code + form_code
```

---

## 7.8 CustomFormVersion — `config_custom_form_version`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
form_definition_id UUID NOT NULL
version_number INT NOT NULL
status VARCHAR(50) NOT NULL
schema_json JSONB NULL
published_at TIMESTAMP NULL
published_by UUID NULL
created_at / created_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique form_definition_id + version_number
```

---

## 7.9 CustomFormSection — `config_custom_form_section`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
form_version_id UUID NOT NULL
section_code VARCHAR(150) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
sort_order INT NOT NULL DEFAULT 0
visibility_config_json JSONB NULL
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.10 CustomFormField — `config_custom_form_field`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
form_version_id UUID NOT NULL
section_id UUID NULL
field_source VARCHAR(50) NOT NULL
core_field_key VARCHAR(150) NULL
custom_field_definition_id UUID NULL
label_override VARCHAR(255) NULL
required_on_form BOOLEAN NOT NULL DEFAULT false
readonly BOOLEAN NOT NULL DEFAULT false
hidden BOOLEAN NOT NULL DEFAULT false
layout_config_json JSONB NULL
sort_order INT NOT NULL DEFAULT 0
created_at / created_by
updated_at / updated_by
version INT
```

Field source:

```text
CORE_FIELD
CUSTOM_FIELD
READONLY_DISPLAY
INSTRUCTION_TEXT
SEPARATOR
```

---

## 7.11 FormSubmission — `config_form_submission`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
form_definition_id UUID NOT NULL
form_version_id UUID NOT NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NULL
principal_type VARCHAR(50) NOT NULL
submitted_by_user_id UUID NULL
submitted_by_external_portal_account_id UUID NULL
payload_json JSONB NOT NULL
validation_status VARCHAR(50) NOT NULL
validation_errors_json JSONB NULL
result_target_id UUID NULL
status VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
processed_at TIMESTAMP NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
SUBMITTED
VALIDATION_FAILED
PROCESSED
FAILED
CANCELLED
```

---

## 7.12 TagDefinition — `config_tag_definition`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
tag_code VARCHAR(150) NOT NULL
label VARCHAR(255) NOT NULL
description TEXT NULL
color VARCHAR(50) NULL
allowed_object_types_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + tag_code
```

---

## 7.13 TagAssignment — `config_tag_assignment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
tag_definition_id UUID NOT NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique active tag_definition_id + object_type_code + target_id
```

---

## 7.14 Taxonomy — `config_taxonomy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
taxonomy_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.15 TaxonomyTerm — `config_taxonomy_term`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
taxonomy_id UUID NOT NULL
parent_term_id UUID NULL
term_code VARCHAR(150) NOT NULL
label VARCHAR(255) NOT NULL
description TEXT NULL
sort_order INT NOT NULL DEFAULT 0
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique taxonomy_id + parent_term_id + term_code
```

---

## 7.16 StatusSet — `config_status_set`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
object_type_code VARCHAR(100) NOT NULL
status_set_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
current_version_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.17 StatusSetValue — `config_status_set_value`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
status_set_id UUID NOT NULL
status_code VARCHAR(150) NOT NULL
label VARCHAR(255) NOT NULL
domain_status_category VARCHAR(100) NOT NULL
color VARCHAR(50) NULL
sort_order INT NOT NULL DEFAULT 0
terminal BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.18 LayoutDefinition — `config_layout_definition`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
layout_code VARCHAR(150) NOT NULL
layout_type VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
layout_json JSONB NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Object type APIs

```text
GET /api/config/object-types
GET /api/config/object-types/{objectTypeCode}
```

Admin seed/internal optional:

```text
POST /api/admin/config/object-types/seed
```

---

## 8.2 Custom field APIs

```text
POST  /api/workspaces/{workspaceId}/config/custom-fields
GET   /api/workspaces/{workspaceId}/config/custom-fields
GET   /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}
PUT   /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}
PATCH /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}/archive

POST  /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}/options
GET   /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}/options
PUT   /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}/options/{optionId}
PATCH /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}/options/{optionId}/archive

POST  /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}/validation-rules
GET   /api/workspaces/{workspaceId}/config/custom-fields/{fieldId}/validation-rules
PUT   /api/workspaces/{workspaceId}/config/custom-field-validation-rules/{ruleId}
DELETE /api/workspaces/{workspaceId}/config/custom-field-validation-rules/{ruleId}
```

---

## 8.3 Custom field value APIs

Generic:

```text
GET /api/workspaces/{workspaceId}/config/custom-field-values?objectType=TASK&targetId=...
PUT /api/workspaces/{workspaceId}/config/custom-field-values?objectType=TASK&targetId=...
```

Convenience target APIs:

```text
GET /api/projects/{projectId}/tasks/{taskId}/custom-fields
PUT /api/projects/{projectId}/tasks/{taskId}/custom-fields

GET /api/projects/{projectId}/requirements/{requirementId}/custom-fields
PUT /api/projects/{projectId}/requirements/{requirementId}/custom-fields

GET /api/projects/{projectId}/defects/{defectId}/custom-fields
PUT /api/projects/{projectId}/defects/{defectId}/custom-fields
```

Only implement convenience endpoints for modules that exist.

---

## 8.4 Form definition APIs

```text
POST  /api/workspaces/{workspaceId}/config/forms
GET   /api/workspaces/{workspaceId}/config/forms
GET   /api/workspaces/{workspaceId}/config/forms/{formId}
PUT   /api/workspaces/{workspaceId}/config/forms/{formId}
PATCH /api/workspaces/{workspaceId}/config/forms/{formId}/archive

POST /api/workspaces/{workspaceId}/config/forms/{formId}/versions
GET  /api/workspaces/{workspaceId}/config/forms/{formId}/versions
PUT  /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}
POST /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}/publish
POST /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}/make-current
```

---

## 8.5 Form section/field APIs

```text
POST   /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}/sections
GET    /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}/sections
PUT    /api/workspaces/{workspaceId}/config/form-sections/{sectionId}
DELETE /api/workspaces/{workspaceId}/config/form-sections/{sectionId}

POST   /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}/fields
GET    /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}/fields
PUT    /api/workspaces/{workspaceId}/config/form-fields/{formFieldId}
DELETE /api/workspaces/{workspaceId}/config/form-fields/{formFieldId}
PUT    /api/workspaces/{workspaceId}/config/forms/{formId}/versions/{versionId}/fields/reorder
```

---

## 8.6 Form submission APIs

Internal:

```text
POST /api/workspaces/{workspaceId}/config/forms/{formId}/submit
GET  /api/workspaces/{workspaceId}/config/form-submissions
GET  /api/workspaces/{workspaceId}/config/form-submissions/{submissionId}
```

Portal if Phase 30 exists:

```text
POST /api/portal/projects/{projectId}/forms/{formId}/submit
GET  /api/portal/projects/{projectId}/forms/{formId}
```

---

## 8.7 Tags APIs

```text
POST  /api/workspaces/{workspaceId}/config/tags
GET   /api/workspaces/{workspaceId}/config/tags
GET   /api/workspaces/{workspaceId}/config/tags/{tagId}
PUT   /api/workspaces/{workspaceId}/config/tags/{tagId}
PATCH /api/workspaces/{workspaceId}/config/tags/{tagId}/archive

POST   /api/workspaces/{workspaceId}/config/tag-assignments
GET    /api/workspaces/{workspaceId}/config/tag-assignments
DELETE /api/workspaces/{workspaceId}/config/tag-assignments/{assignmentId}
```

---

## 8.8 Taxonomy APIs

```text
POST  /api/workspaces/{workspaceId}/config/taxonomies
GET   /api/workspaces/{workspaceId}/config/taxonomies
GET   /api/workspaces/{workspaceId}/config/taxonomies/{taxonomyId}
PUT   /api/workspaces/{workspaceId}/config/taxonomies/{taxonomyId}
PATCH /api/workspaces/{workspaceId}/config/taxonomies/{taxonomyId}/archive

POST  /api/workspaces/{workspaceId}/config/taxonomies/{taxonomyId}/terms
GET   /api/workspaces/{workspaceId}/config/taxonomies/{taxonomyId}/terms
PUT   /api/workspaces/{workspaceId}/config/taxonomy-terms/{termId}
PATCH /api/workspaces/{workspaceId}/config/taxonomy-terms/{termId}/archive
```

---

## 8.9 Status/category/layout APIs

```text
POST  /api/workspaces/{workspaceId}/config/status-sets
GET   /api/workspaces/{workspaceId}/config/status-sets
GET   /api/workspaces/{workspaceId}/config/status-sets/{statusSetId}
PUT   /api/workspaces/{workspaceId}/config/status-sets/{statusSetId}
PATCH /api/workspaces/{workspaceId}/config/status-sets/{statusSetId}/archive

POST /api/workspaces/{workspaceId}/config/status-sets/{statusSetId}/values
GET  /api/workspaces/{workspaceId}/config/status-sets/{statusSetId}/values
PUT  /api/workspaces/{workspaceId}/config/status-set-values/{valueId}
PATCH /api/workspaces/{workspaceId}/config/status-set-values/{valueId}/archive

POST  /api/workspaces/{workspaceId}/config/layouts
GET   /api/workspaces/{workspaceId}/config/layouts
GET   /api/workspaces/{workspaceId}/config/layouts/{layoutId}
PUT   /api/workspaces/{workspaceId}/config/layouts/{layoutId}
PATCH /api/workspaces/{workspaceId}/config/layouts/{layoutId}/archive
```

---

## 8.10 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/custom-fields
GET /api/workspaces/{workspaceId}/reports/custom-field-usage
GET /api/workspaces/{workspaceId}/reports/forms
GET /api/workspaces/{workspaceId}/reports/tags
GET /api/workspaces/{workspaceId}/reports/config-audit
```

---

# 9. Authorization requirements

Required authorities:

```text
CONFIG_OBJECT_TYPE_VIEW

CUSTOM_FIELD_VIEW
CUSTOM_FIELD_CREATE
CUSTOM_FIELD_UPDATE
CUSTOM_FIELD_ARCHIVE
CUSTOM_FIELD_OPTION_MANAGE
CUSTOM_FIELD_VALIDATION_MANAGE
CUSTOM_FIELD_VALUE_VIEW
CUSTOM_FIELD_VALUE_UPDATE
CUSTOM_FIELD_SENSITIVE_VALUE_VIEW
CUSTOM_FIELD_SENSITIVE_VALUE_UPDATE

CUSTOM_FORM_VIEW
CUSTOM_FORM_CREATE
CUSTOM_FORM_UPDATE
CUSTOM_FORM_PUBLISH
CUSTOM_FORM_ARCHIVE
CUSTOM_FORM_SUBMIT
CUSTOM_FORM_SUBMISSION_VIEW

TAG_VIEW
TAG_CREATE
TAG_UPDATE
TAG_ARCHIVE
TAG_ASSIGN
TAG_UNASSIGN

TAXONOMY_VIEW
TAXONOMY_CREATE
TAXONOMY_UPDATE
TAXONOMY_ARCHIVE
TAXONOMY_TERM_MANAGE

STATUS_SET_VIEW
STATUS_SET_CREATE
STATUS_SET_UPDATE
STATUS_SET_ARCHIVE

LAYOUT_VIEW
LAYOUT_CREATE
LAYOUT_UPDATE
LAYOUT_ARCHIVE

CONFIG_REPORT_VIEW
CONFIG_AUDIT_VIEW
```

Portal capabilities:

```text
PORTAL_FORM_VIEW
PORTAL_FORM_SUBMIT
PORTAL_CUSTOM_FIELD_VIEW
```

Rules:

```text
1. Workspace config permissions required for definitions.
2. Target object permission required for custom field value update.
3. Sensitive custom fields require stronger permission.
4. Portal form submission requires portal grant and visible form.
5. Config does not grant object access.
```

---

# 10. Lifecycle rules

## 10.1 CustomFieldDefinition lifecycle

```text
ACTIVE → ARCHIVED
```

Rules:

```text
1. Field type immutable after values exist.
2. Archived field hidden from new forms.
3. Existing values remain auditable.
4. Sensitive/client-visible changes audited.
```

## 10.2 Form lifecycle

```text
DRAFT → PUBLISHED
PUBLISHED → ARCHIVED
```

Rules:

```text
1. Draft form version editable.
2. Published form version immutable.
3. New changes create new version.
4. Current version unique per form.
```

## 10.3 Tag/taxonomy lifecycle

```text
ACTIVE → ARCHIVED
```

Rules:

```text
1. Archived tag/term not available for new assignments.
2. Historical assignments remain.
```

## 10.4 StatusSet lifecycle

```text
DRAFT → PUBLISHED
PUBLISHED → ARCHIVED
```

Rules:

```text
1. Published status values immutable except new version if versioning implemented.
2. Status maps to domain status category.
3. Cannot break core lifecycle.
```

---

# 11. Validation and data type rules

## 11.1 Type validation

Examples:

```text
TEXT -> string max length
NUMBER -> integer
DECIMAL -> decimal precision
CURRENCY -> amount + optional currency
DATE -> ISO date
DATETIME -> ISO timestamp
BOOLEAN -> boolean
SELECT -> one active option
MULTI_SELECT -> active option list within max
USER -> existing workspace user
EXTERNAL_CONTACT -> existing external contact in workspace
DOCUMENT -> existing visible document
URL -> valid URL
EMAIL -> valid email
PHONE -> length/pattern
```

## 11.2 Sensitive values

Rules:

```text
1. Sensitive values masked by default in list/search/report/export.
2. Sensitive values require explicit permission.
3. Sensitive values excluded from portal unless explicitly allowed and reviewed.
4. Audit access to sensitive values if policy enabled.
```

## 11.3 Required fields

Rules:

```text
1. Required at field definition may apply globally where enabled.
2. Required at form field applies to that form submission only.
3. Existing objects may be incomplete until edited unless backfill policy exists.
4. Reports can show missing required custom fields.
```

---

# 12. Integration rules

## 12.1 Project/task/requirement integration

Rules:

```text
1. Supported object types can read/write custom fields.
2. Object APIs may embed custom field values if requested.
3. Updates go through validation and target permission.
4. Audit records changes.
```

## 12.2 Search integration

Rules:

```text
1. Searchable custom fields can be indexed.
2. Sensitive custom fields excluded from snippets.
3. Custom field filters supported where practical.
4. Search index updated after value change.
```

## 12.3 Reporting/export integration

Rules:

```text
1. Reportable custom fields available as columns.
2. Exportable field policy enforced.
3. Saved views can include custom fields.
4. Hidden/sensitive fields masked.
```

## 12.4 Portal integration

Rules:

```text
1. Portal forms only include portal-visible fields.
2. Portal custom field values only appear if target and field visible.
3. Portal submission uses external principal.
4. Portal submission cannot set internal-only fields.
```

## 12.5 Workflow integration

Phase 34 will use:

```text
custom fields in workflow conditions
forms as workflow input
status sets as workflow states where safe
```

Phase 33 only stores config and values.

## 12.6 Document/form integration

Rules:

```text
1. Form submissions can attach/link Document if field type DOCUMENT.
2. Document access still checked by DocumentHub.
3. Custom field document references do not grant document access.
```

---

# 13. Reporting integration

Extend Phase 22 with:

```text
CUSTOM_FIELD_DEFINITION_REPORT
CUSTOM_FIELD_USAGE_REPORT
CUSTOM_FIELD_MISSING_REQUIRED_REPORT
FORM_DEFINITION_REPORT
FORM_SUBMISSION_REPORT
TAG_USAGE_REPORT
TAXONOMY_USAGE_REPORT
CONFIG_AUDIT_REPORT
```

Dashboard KPIs:

```text
customFieldCount
sensitiveCustomFieldCount
portalVisibleCustomFieldCount
publishedForms
formSubmissionsThisPeriod
missingRequiredCustomFieldCount
tagCount
configChangesThisPeriod
```

---

# 14. AI integration

AI can suggest:

```text
field definitions
form layouts
tag taxonomy
status labels
validation rules
portal visibility review
migration/backfill checklist
```

Rules:

```text
1. AI suggestions are proposal only.
2. AI cannot publish configuration.
3. AI cannot create custom scripts.
4. AI cannot mark sensitive field as portal-visible without human review.
5. AI must respect current user's config permissions.
```

---

# 15. Event Registry integration

Recommended source system:

```text
SCOPERY_CONFIGURATION
```

Required events:

```text
CONFIG_OBJECT_TYPE_SEEDED

CUSTOM_FIELD_CREATED
CUSTOM_FIELD_UPDATED
CUSTOM_FIELD_ARCHIVED
CUSTOM_FIELD_SENSITIVE_FLAG_CHANGED
CUSTOM_FIELD_CLIENT_VISIBLE_CHANGED
CUSTOM_FIELD_OPTION_CREATED
CUSTOM_FIELD_OPTION_UPDATED
CUSTOM_FIELD_OPTION_ARCHIVED
CUSTOM_FIELD_VALUE_UPDATED
CUSTOM_FIELD_VALIDATION_RULE_CREATED
CUSTOM_FIELD_VALIDATION_RULE_UPDATED
CUSTOM_FIELD_VALIDATION_RULE_REMOVED

CUSTOM_FORM_CREATED
CUSTOM_FORM_UPDATED
CUSTOM_FORM_VERSION_CREATED
CUSTOM_FORM_VERSION_PUBLISHED
CUSTOM_FORM_VERSION_ARCHIVED
CUSTOM_FORM_SUBMITTED
CUSTOM_FORM_SUBMISSION_FAILED

TAG_CREATED
TAG_UPDATED
TAG_ARCHIVED
TAG_ASSIGNED
TAG_UNASSIGNED

TAXONOMY_CREATED
TAXONOMY_UPDATED
TAXONOMY_ARCHIVED
TAXONOMY_TERM_CREATED
TAXONOMY_TERM_UPDATED
TAXONOMY_TERM_ARCHIVED

STATUS_SET_CREATED
STATUS_SET_UPDATED
STATUS_SET_ARCHIVED
STATUS_SET_VALUE_CREATED
STATUS_SET_VALUE_UPDATED
STATUS_SET_VALUE_ARCHIVED

LAYOUT_CREATED
LAYOUT_UPDATED
LAYOUT_ARCHIVED
```

Standard variables:

```text
actor.userId
externalPortalAccount.id
workspace.id
project.id
objectType.code
customField.id
customField.key
form.id
formVersion.id
tag.id
taxonomy.id
taxonomyTerm.id
statusSet.id
layout.id
target.type
target.id
occurredAt
traceId
```

---

# 16. Audit / activity / outbox

Audit-sensitive actions:

```text
sensitive custom field created/updated
client-visible custom field enabled
portal-visible form published
custom field value containing sensitive data viewed/changed
status set published
validation rule changed
layout with sensitive field published
form submission from external portal
```

Activity actions:

```text
CUSTOM_FIELD_CREATED
CUSTOM_FORM_VERSION_PUBLISHED
TAG_ASSIGNED
CUSTOM_FIELD_VALUE_UPDATED
```

Outbox required for major config events if search/report/workflow consume them.

Idempotency recommended for:

```text
POST /custom-fields
POST /forms
POST /forms/{id}/versions/{versionId}/publish
PUT /custom-field-values
POST /tag-assignments
POST /form submit
```

---

# 17. Business rules master

## 17.1 Object type rules

```text
OBJ-001 Object type code must be seeded and stable.
OBJ-002 Object type does not create domain entity.
OBJ-003 Feature must be enabled before using custom fields/forms/tags.
```

## 17.2 Custom field rules

```text
CF-001 Field key unique per workspace/objectType.
CF-002 Field type required.
CF-003 Field type immutable after values exist.
CF-004 Sensitive field masked by default.
CF-005 Client-visible field cannot be sensitive unless override/review.
CF-006 Archived field unavailable for new forms.
CF-007 Custom field does not bypass target object permission.
CF-008 Custom field value target must match object type.
```

## 17.3 Validation rules

```text
VAL-001 Validation declarative only.
VAL-002 Regex complexity limited.
VAL-003 Failed validation prevents mutation.
VAL-004 Validation error structured.
```

## 17.4 Form rules

```text
FORM-001 Form belongs to object type.
FORM-002 Published form immutable.
FORM-003 Form fields must reference valid core/custom fields.
FORM-004 Form submission uses domain service.
FORM-005 Portal form cannot include internal-only fields.
FORM-006 Failed submission does not mutate target.
```

## 17.5 Tag/taxonomy rules

```text
TAG-001 Tag does not grant access.
TAG-002 Target object type restriction enforced.
TAG-003 Duplicate assignment prevented.
TAX-001 Term code unique under taxonomy parent.
TAX-002 Archived term unavailable for new selection.
```

## 17.6 Status/layout rules

```text
STAT-001 Custom status maps to domain status category.
STAT-002 Custom status cannot skip domain lifecycle.
LAY-001 Layout references allowed fields only.
LAY-002 Layout does not grant access.
```

---

# 18. Error catalog

```text
CONFIG_OBJECT_TYPE_NOT_FOUND
CONFIG_OBJECT_TYPE_FEATURE_DISABLED
CONFIG_ACCESS_DENIED

CUSTOM_FIELD_NOT_FOUND
CUSTOM_FIELD_KEY_ALREADY_EXISTS
CUSTOM_FIELD_TYPE_IMMUTABLE
CUSTOM_FIELD_TYPE_INVALID
CUSTOM_FIELD_ARCHIVED
CUSTOM_FIELD_SENSITIVE_ACCESS_DENIED
CUSTOM_FIELD_CLIENT_VISIBLE_REJECTED
CUSTOM_FIELD_VALUE_INVALID_TYPE
CUSTOM_FIELD_VALUE_TARGET_MISMATCH
CUSTOM_FIELD_VALUE_REQUIRED
CUSTOM_FIELD_OPTION_NOT_FOUND
CUSTOM_FIELD_OPTION_DUPLICATE
CUSTOM_FIELD_VALIDATION_FAILED
CUSTOM_FIELD_VALIDATION_RULE_INVALID

CUSTOM_FORM_NOT_FOUND
CUSTOM_FORM_VERSION_NOT_FOUND
CUSTOM_FORM_VERSION_IMMUTABLE
CUSTOM_FORM_FIELD_INVALID
CUSTOM_FORM_FIELD_TARGET_MISMATCH
CUSTOM_FORM_PORTAL_INTERNAL_FIELD_REJECTED
CUSTOM_FORM_SUBMISSION_NOT_FOUND
CUSTOM_FORM_SUBMISSION_VALIDATION_FAILED
CUSTOM_FORM_SUBMISSION_PROCESSING_FAILED

TAG_NOT_FOUND
TAG_CODE_ALREADY_EXISTS
TAG_OBJECT_TYPE_NOT_ALLOWED
TAG_ASSIGNMENT_DUPLICATE
TAG_ASSIGNMENT_TARGET_MISMATCH

TAXONOMY_NOT_FOUND
TAXONOMY_CODE_ALREADY_EXISTS
TAXONOMY_TERM_NOT_FOUND
TAXONOMY_TERM_CODE_ALREADY_EXISTS

STATUS_SET_NOT_FOUND
STATUS_SET_VALUE_NOT_FOUND
STATUS_SET_INVALID_DOMAIN_MAPPING
STATUS_SET_LIFECYCLE_VIOLATION

LAYOUT_NOT_FOUND
LAYOUT_INVALID_FIELD_REFERENCE
LAYOUT_INTERNAL_FIELD_PORTAL_REJECTED
```

---

# 19. Required tests

## 19.1 Object type tests

```text
objectTypeSeeder_firstRun_createsDefaults
objectTypeSeeder_secondRun_noDuplicates
objectTypeFeatureDisabled_rejectsCustomField
```

## 19.2 Custom field tests

```text
createCustomField_valid_success
createCustomField_duplicateKey_rejected
updateCustomField_success
changeFieldTypeAfterValues_rejected
archiveCustomField_success
createSelectOptions_success
archivedOptionUnavailableForNewValue
setCustomFieldValue_text_success
setCustomFieldValue_wrongType_rejected
setCustomFieldValue_targetObjectTypeMismatch_rejected
requiredCustomField_missing_rejectedWhenPolicyEnabled
sensitiveCustomField_maskedWithoutPermission
clientVisibleSensitiveField_requiresOverride
```

## 19.3 Validation rule tests

```text
minLengthValidation_success
maxValueValidation_success
regexValidation_success
regexTooComplex_rejected
uniqueWithinProject_validation_success
failedValidation_preventsMutation
```

## 19.4 Form tests

```text
createForm_valid_success
createFormVersion_success
addFormSection_success
addCustomFieldToForm_success
addFieldWrongObjectType_rejected
publishFormVersion_success
publishedFormVersion_immutable
submitForm_valid_createsOrUpdatesTarget
submitForm_validationFailed_noMutation
portalForm_internalFieldRejected
```

## 19.5 Tag/taxonomy tests

```text
createTag_success
createTag_duplicateCode_rejected
assignTag_success
assignTag_wrongObjectType_rejected
tagDoesNotGrantAccess
createTaxonomy_success
createTaxonomyTerm_success
archiveTaxonomyTerm_preservesHistory
```

## 19.6 Status/layout tests

```text
createStatusSet_success
customStatusMustMapToDomainCategory
customStatusCannotSkipLifecycle
createLayout_success
layoutInvalidFieldReference_rejected
portalLayoutInternalField_rejected
layoutDoesNotGrantFieldAccess
```

## 19.7 Integration tests

```text
searchIndexesSearchableCustomField
searchDoesNotLeakSensitiveCustomField
reportIncludesReportableCustomField
exportOmitsNonExportableCustomField
savedViewIncludesCustomField
portalShowsOnlyPortalVisibleCustomFields
documentReferenceCustomFieldDoesNotGrantDocumentAccess
```

## 19.8 Authorization tests

```text
createCustomField_withoutPermission_forbidden
updateSensitiveField_withoutPermission_forbidden
setCustomFieldValue_withoutTargetPermission_forbidden
publishForm_withoutPermission_forbidden
assignTag_withoutPermission_forbidden
viewSensitiveValue_withoutPermission_maskedOrForbidden
crossWorkspaceCustomField_forbidden
```

## 19.9 Event/audit tests

```text
configurationEventSeeder_firstRun_createsDefinitions
configurationEventSeeder_secondRun_noDuplicates
customFieldCreated_eventEmitted
sensitiveFlagChanged_auditCreated
portalFormPublished_auditCreated
customFieldValueUpdated_auditCreated
tagAssigned_eventEmitted
```

---

# 20. Manual verification checklist

Completion file must include:

```text
1. Seed configurable object types.
2. Create custom field for Task.
3. Add select options if applicable.
4. Set custom field value on a Task.
5. Confirm wrong type validation fails.
6. Confirm target object type mismatch fails.
7. Mark field sensitive and confirm masking.
8. Create custom form for Requirement intake.
9. Add sections and fields.
10. Publish form version.
11. Submit form internally and confirm domain action runs.
12. Submit portal form if Phase 30 exists and confirm internal fields hidden.
13. Create tags and assign to objects.
14. Create taxonomy and terms.
15. Create status set with domain mapping.
16. Confirm custom status cannot bypass lifecycle.
17. Add custom field to saved view/report/search.
18. Confirm no dynamic DB schema/code/workflow automation is falsely claimed.
```

---

# 21. Acceptance criteria

Phase 33 is accepted only if:

```text
1. Current configuration/custom-field capability is classified against TO-BE.
2. ConfigurableObjectType implemented/seeded/tested.
3. CustomFieldDefinition implemented/tested.
4. CustomFieldOption implemented/tested.
5. CustomFieldValue implemented/tested.
6. CustomFieldValidationRule implemented/tested.
7. FieldVisibilityPolicy implemented/tested.
8. CustomFormDefinition/Version/Section/Field implemented/tested.
9. FormSubmission implemented/tested.
10. TagDefinition/TagAssignment implemented/tested.
11. Taxonomy/TaxonomyTerm implemented/tested.
12. StatusSet/StatusSetValue foundation implemented/tested where safe.
13. LayoutDefinition implemented/tested.
14. Search/report/export/saved view/portal integrations implemented/tested or explicitly deferred.
15. IAM permissions implemented/tested.
16. Event seeders idempotent.
17. Activity/audit/outbox follows Phase 04.
18. No no-code app builder, dynamic DB schema, arbitrary scripting, workflow automation, or frontend builder is falsely claimed.
19. `mvn compile` passes.
20. `mvn test` passes.
21. Completion file exists.
```

Do not mark complete if:

```text
custom fields bypass target object permission
custom field value type not validated
sensitive values leak in search/report/export/portal
published forms can be silently changed
custom statuses bypass domain lifecycle
tags grant access
dynamic SQL/code execution is allowed
tests fail
```

---

# 22. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_33_CUSTOM_FIELDS_FORMS_CONFIGURATION_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 33 — Custom Fields / Forms / Configuration Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Configuration Boundary Decision
## 9. Storage Strategy
## 10. Entity Mapping
## 11. API Changes
## 12. Configurable Object Type Strategy
## 13. Custom Field Definition Strategy
## 14. Custom Field Value Strategy
## 15. Validation Strategy
## 16. Field Visibility / Sensitivity Strategy
## 17. Custom Form Strategy
## 18. Form Submission Strategy
## 19. Tag / Taxonomy Strategy
## 20. Status / Category Strategy
## 21. Layout Strategy
## 22. Search / Report / Export Integration
## 23. Portal Integration
## 24. AI Suggestion Strategy
## 25. Authorization Matrix
## 26. Notification / Event Strategy
## 27. Activity / Audit / Outbox Notes
## 28. Idempotency Strategy
## 29. Tests Added
## 30. Commands Run
## 31. Test Results
## 32. Manual Verification
## 33. Assumptions
## 34. Deviations From Prompt
## 35. Known Risks
## 36. Future Phases That Must Return to Configuration
```

---

# 23. Future phases that must return

```text
Phase 34 — Workflow / Approval:
- Use custom fields/forms/statuses in workflow conditions and approval forms.

Phase 35 — Advanced Notifications:
- Notification rules based on custom fields.

Phase 38 — Audit / Compliance / Privacy:
- Data retention, privacy export/delete for custom fields.

Phase 39 — Integration / Import / Export:
- Import/export custom field definitions and values, external form integration.

Phase 41 — Data Quality / Knowledge Graph / Semantic Index:
- Semantic classification, custom field quality checks, taxonomy intelligence.

Future No-code App Builder Backlog:
- Custom object types, app builder, dynamic entity builder.
```

---

# 24. Agent anti-bịa rules

The agent must not:

```text
1. Claim dynamic database schema changes exist.
2. Claim arbitrary custom scripting exists.
3. Claim no-code app builder exists.
4. Claim workflow automation exists.
5. Claim frontend form builder exists.
6. Let custom fields bypass IAM.
7. Let custom statuses bypass domain lifecycle.
8. Let sensitive custom values leak to search/report/export/portal.
9. Let tags grant access.
10. Silently edit published forms.
11. Hide deferred workflow/no-code/automation gaps.
```

---

# 25. Prompt to give coding agent

```text
You are implementing Phase 33 — TO-BE Custom Fields, Dynamic Forms, Object Configuration, Taxonomy, Tags & Workspace Configuration Layer.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–32 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current configuration/custom-field/form capability against this Phase 33 TO-BE spec.
2. Classify each capability with required labels.
3. Implement ConfigurableObjectType seed/config.
4. Implement CustomFieldDefinition, CustomFieldOption, CustomFieldValue, CustomFieldValidationRule, FieldVisibilityPolicy.
5. Implement CustomFormDefinition, CustomFormVersion, CustomFormSection, CustomFormField, FormSubmission.
6. Implement TagDefinition, TagAssignment, Taxonomy, TaxonomyTerm.
7. Implement safe StatusSet/StatusSetValue and LayoutDefinition foundation.
8. Integrate with Search, Reporting, Saved Views, Export, Portal, Documents, and target domain objects where available.
9. Add IAM permissions, events, audit/outbox, idempotency, and tests.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_33_CUSTOM_FIELDS_FORMS_CONFIGURATION_TO_BE_COMPLETE.md.

Do not implement or claim arbitrary database schema generation, custom scripting, no-code app builder, frontend UI builder, workflow automation, or dynamic code execution in this phase.
```

---

# 26. Quick tracking matrix

| Capability | Current backend | Phase 33 action | Later phase |
|---|---|---|---|
| ConfigurableObjectType | Missing/unknown | Must seed/implement | — |
| CustomFieldDefinition | Missing/unknown | Must implement | — |
| CustomFieldOption | Missing/unknown | Must implement | — |
| CustomFieldValue | Missing/unknown | Must implement | — |
| ValidationRule | Missing/unknown | Must implement declarative | — |
| FieldVisibilityPolicy | Missing/unknown | Must implement | Privacy Phase 38 |
| CustomFormDefinition | Missing/unknown | Must implement | Workflow Phase 34 |
| CustomFormVersion | Missing/unknown | Must implement | — |
| CustomFormSection | Missing/unknown | Must implement | — |
| CustomFormField | Missing/unknown | Must implement | — |
| FormSubmission | Missing/unknown | Must implement | Workflow Phase 34 |
| TagDefinition | Missing/unknown | Must implement | — |
| TagAssignment | Missing/unknown | Must implement | — |
| Taxonomy | Missing/unknown | Must implement | Semantic Phase 41 |
| TaxonomyTerm | Missing/unknown | Must implement | Semantic Phase 41 |
| StatusSet | Missing/unknown | Must implement safe mapping | Workflow Phase 34 |
| LayoutDefinition | Missing/unknown | Must implement metadata | Frontend rendering |
| Search integration | Missing/partial | Must implement | Semantic Phase 41 |
| Report/export integration | Missing/partial | Must implement | — |
| Portal integration | Missing/partial | Must implement if Phase 30 | — |
| Workflow automation | Missing | Defer | Phase 34 |
| Custom scripting | Missing | Not in scope | Maybe never / strict review |
| No-code app builder | Missing | Defer | Future backlog |
| Dynamic DB schema | Missing | Not in scope | Avoid |

---

# 27. Final principle

Phase 33 is not complete when "a JSON field can be saved."

Phase 33 is complete when Scopery can safely adapt to different workspaces:

```text
object type metadata
+ custom field definition
+ typed validated values
+ forms
+ tags / taxonomy
+ safe status mapping
+ visibility policy
+ search/report/export integration
+ audit
= configurable Work OS without unsafe dynamic code
```

Custom field is not database schema.

Form is not workflow.

Layout is not frontend builder.

Tag is not permission.

Custom status is not domain lifecycle override.

Configuration must extend Scopery safely, not break it.
