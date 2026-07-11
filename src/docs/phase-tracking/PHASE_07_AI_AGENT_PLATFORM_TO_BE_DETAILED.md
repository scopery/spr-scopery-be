# PHASE 07 — TO-BE AI Agent Platform, Model Governance, Prompt Versioning, Event Config, Execution Log & Usage Policy

> Project: Scopery Backend  
> Phase: 07  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine  
> API base: `/api`  
> Primary module: `modules/aiagent`  
> Related modules: `iam`, `workspace`, `eventregistry`, `notification`, `knowledge`, future `project`, `document`, `workflow`, `reporting`  
> Important rule: This file is **not an as-is description**. It defines the TO-BE AI Agent platform foundation, compares it to the current AI Agent module, and marks what must be implemented now vs deferred.

---

# 0. Purpose of this file

Phase 07 defines the AI Agent platform foundation for Scopery.

Scopery will eventually use AI for:

```text
Project planning
WBS generation
Task breakdown
Schedule suggestions
Capacity risk analysis
Finance/margin explanation
Quote drafting
Change request impact analysis
Report summaries
Document knowledge retrieval
Workflow recommendation
Support/service triage
```

But AI must not become an uncontrolled side-effect system.

This phase ensures AI is governed by:

```text
Provider registry
Model registry
Deployment management
Agent configuration
Prompt template and prompt version control
Event-driven agent execution configuration
Execution logging
Usage policy
Safety gates
IAM integration
Event Registry integration
Notification integration
Cost/usage tracking foundation
```

Phase 07 is **AI platform foundation**, not full AI-assisted project planning. AI-assisted project planning is Phase 21.

---

# 1. Source inputs

Before coding Phase 07, the agent must read:

```text
1. Current backend codebase
2. Current BE feature/entity/business-rule inventory
3. Dynamic Work OS future-state feature catalog
4. Phase 00 master roadmap
5. Phase 01 API/security baseline
6. Phase 02 IAM TO-BE spec
7. Phase 03 Workspace TO-BE spec
8. Phase 04 Platform Audit/Outbox/Idempotency spec
9. Phase 05 Event Registry TO-BE spec
10. Phase 06 Notification TO-BE spec
11. Existing AI Agent module code, migrations, seeders, tests
12. Existing EventRegistry integration
13. Existing notification/email/outbox patterns
14. Existing IAM permission model
15. Existing Knowledge/DocumentType module
```

The agent must not implement from current code only.

---

# 2. Current backend snapshot

Current backend inventory says the AI Agent module currently has:

```text
15 action-level functions
81 business rules
```

Current AI Agent entities are expected to include:

```text
AiProvider
AiModel
AiModelDeployment
AiAgent
AiPromptTemplate
AiPromptVersion
AiEventConfig
AiExecutionLog
AiUsagePolicy
```

Current AI Agent functions likely include:

```text
Provider create/update/lifecycle
Model create/update/lifecycle
Deployment create/update/activate/deactivate
Agent create/update/lifecycle
Prompt template create/update/lifecycle
Prompt version create/activate
Event config create/activate/deactivate
Execute event
Execution log query
Usage policy create/update/activate
```

This is a strong foundation, but not a full Work OS AI governance platform.

---

# 3. Future-state Work OS AI capability areas

Future Work OS AI Platform and Agent Governance includes capabilities such as:

```text
AI provider governance
Model registry
Model deployment/environment management
Agent registry
Prompt template management
Prompt versioning
Event-triggered agent execution
Manual agent execution
Tool permission
Acting-on-behalf authorization
AI context source control
RAG/document access
Usage policy
Budget/usage limit
Human approval gate
AI suggestion review
AI execution trace
Model/prompt evaluation
Safety and content policy
AI audit
AI notification
AI cost reporting
Agent access review
```

Phase 07 must build the platform foundation and clearly defer advanced features.

---

# 4. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `MUST_IMPLEMENT_IN_PHASE_07` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_07` | Create seeds/policies/events/templates now; full producer/consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return to AI platform. |
| `DEFERRED_TO_POST_23_BACKLOG` | Not in core 00–23 roadmap; keep for full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 5. Phase 07 target statement

Phase 07 must deliver a future-ready AI Agent platform core:

```text
1. AI providers can be registered safely.
2. AI models can be registered under providers.
3. AI deployments bind model + environment + endpoint/config.
4. AI agents can be configured and activated.
5. Prompt templates and prompt versions are versioned and immutable once active/published.
6. Event configs bind EventRegistry events to agents/prompts/deployments.
7. Execute-event flow is controlled by EventDefinition and UsagePolicy.
8. Every AI execution is logged with traceId, event, agent, prompt version, model deployment, status, token/cost fields where available.
9. Usage policies prevent unsafe/unbounded execution.
10. AI provider credentials are never exposed/logged.
11. AI actions do not mutate business data directly without explicit later-phase human approval flow.
12. IAM permissions protect all AI admin operations.
13. AI events and notifications are seeded or documented.
14. Future AI project planning, RAG, tools, acting-on-behalf, evaluation and cost analytics are explicitly deferred with phase owners.
```

---

# 6. Phase 07 must implement now vs defer

## 6.1 Must implement / harden in Phase 07

```text
AiProvider lifecycle
AiModel lifecycle
AiModelDeployment lifecycle
AiAgent lifecycle
AiPromptTemplate lifecycle
AiPromptVersion create/activate/immutability
AiEventConfig create/activate/deactivate
ExecuteEvent foundation
AiExecutionLog
AiUsagePolicy
EventRegistry validation
Notification events for AI execution failure/policy block if configured
Provider secret handling
IAM authorization for AI admin
TraceId / audit / activity / outbox integration
Idempotent seeders for AI events and optional default policies
Tests
Completion gap matrix
```

## 6.2 Must not implement in Phase 07

Do not implement full business AI features now:

```text
Generate project plan/WBS/tasks
Apply AI suggestions to project
Acting-on-behalf-of business mutations
AI tool execution
RAG over documents
Vector database integration
Knowledge graph reasoning
Prompt evaluation dashboard
Human feedback scoring
Fine-tuning pipeline
AI budget billing dashboard
AI marketplace/plugin registry
Autonomous workflow execution
```

These are deferred.

---

# 7. TO-BE AI capability matrix

---

## 7.1 AIG-001 — AI provider registry

| Item | Value |
|---|---|
| Future capability | Register approved AI vendors/providers and credentials safely |
| Current state | AiProvider exists |
| Phase 07 target | Harden provider lifecycle and secret safety |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required rules:

```text
1. Provider code unique.
2. Provider name required.
3. Provider type required.
4. Provider status defaults ACTIVE or DRAFT according to current convention.
5. Provider credentials must be encrypted or externalized.
6. API key/secret never returned in API response.
7. API key/secret never logged.
8. Provider cannot be deactivated if active deployments depend on it unless forced/admin.
9. Deactivation blocks new executions.
10. Provider changes are audited.
```

Provider type examples:

```text
OPENAI
AZURE_OPENAI
ANTHROPIC
GOOGLE
LOCAL_LLM
CUSTOM_HTTP
```

Do not hardcode only one provider if current model is generic.

---

## 7.2 AIG-002 — AI model registry

| Item | Value |
|---|---|
| Future capability | Register models under providers with capabilities and constraints |
| Current state | AiModel exists |
| Phase 07 target | Harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required rules:

```text
1. Provider must exist and be ACTIVE.
2. Model code unique per provider.
3. Model name required.
4. Model capability flags optional:
   - chat
   - completion
   - embedding
   - vision
   - toolCalling
   - jsonMode
5. Context window/token limits optional but recommended.
6. Inactive model cannot be deployed.
7. Deactivation blocked if active deployments depend on it unless forced/admin.
```

Suggested fields:

```text
supports_chat
supports_embedding
supports_tool_calling
supports_json_mode
context_window_tokens
max_output_tokens
```

If missing, document as deferred unless needed by current execution code.

---

## 7.3 AIG-003 — Model deployment

| Item | Value |
|---|---|
| Future capability | Bind model to environment/provider endpoint/config for actual execution |
| Current state | AiModelDeployment exists |
| Phase 07 target | Harden deployment lifecycle |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required rules:

```text
1. Deployment references active provider and active model.
2. Deployment environment required.
3. Deployment code unique per environment.
4. Deployment status controls execution.
5. Only ACTIVE deployment can execute.
6. Default deployment per environment/model capability should be unique if supported.
7. Provider credentials are resolved securely at execution time.
8. Deployment config may include endpoint/baseUrl/modelName, but no raw secrets returned.
9. Deactivation blocks new executions.
```

Environment values:

```text
DEV
STAGING
PROD
LOCAL
```

If current project uses a different environment enum, map it.

---

## 7.4 AIG-004 — Agent registry

| Item | Value |
|---|---|
| Future capability | Define AI agent identity, purpose, scope, autonomy, allowed event configs |
| Current state | AiAgent exists |
| Phase 07 target | Harden agent lifecycle and autonomy boundaries |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required fields:

```text
code
name
description
agentType
status
defaultDeploymentId optional
workspaceId optional if workspace-scoped
organizationId optional if org-scoped
autonomyLevel
createdAt/updatedAt
```

Agent type examples:

```text
PROJECT_PLANNER
TASK_ASSISTANT
SCHEDULE_ASSISTANT
FINANCE_ANALYST
QUOTE_ASSISTANT
DOCUMENT_ASSISTANT
REPORT_SUMMARIZER
SYSTEM_AGENT
```

Autonomy level examples:

```text
SUGGEST_ONLY
DRAFT_ONLY
REQUIRES_APPROVAL
AUTO_EXECUTE_READ_ONLY
AUTO_EXECUTE_RESTRICTED
```

Phase 07 rule:

```text
No agent may mutate business data directly in Phase 07.
```

Only allowed now:

```text
execute prompt
record execution
return output
emit AI execution event
```

Business mutation:

```text
DEFERRED_TO_PHASE_21 for project planning suggestions and human approval.
```

---

## 7.5 AIG-005 — Prompt template

| Item | Value |
|---|---|
| Future capability | Manage reusable prompt templates with variable contracts |
| Current state | AiPromptTemplate exists |
| Phase 07 target | Harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required rules:

```text
1. Prompt template code unique.
2. Prompt template belongs to agent or template category.
3. Template status controls version creation/activation.
4. Prompt template can have multiple versions.
5. Template variables must be declared or validated.
6. Prompt template cannot be deleted if versions/executions reference it.
```

Scope:

```text
SYSTEM
ORGANIZATION
WORKSPACE
```

If current scope only system, keep it and defer override.

Deferred:

```text
Workspace prompt override — DEFERRED_TO_PHASE_21 or Phase 39 dynamic config.
```

---

## 7.6 AIG-006 — Prompt version

| Item | Value |
|---|---|
| Future capability | Immutable prompt versioning for audit and repeatability |
| Current state | AiPromptVersion exists |
| Phase 07 target | Harden immutability and activation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required fields:

```text
templateId
versionNumber
systemPrompt
userPromptTemplate
responseFormat
temperature
topP
maxTokens
status
activatedAt
activatedBy
createdAt/createdBy
```

Status:

```text
DRAFT
ACTIVE
ARCHIVED
```

Rules:

```text
1. versionNumber unique per template.
2. DRAFT can be edited.
3. ACTIVE version immutable.
4. Activating version deactivates previous active version if single-active model.
5. Execution log stores promptVersionId.
6. Prompt version cannot reference unknown event variables if event-bound.
7. Prompt output format should be explicit when structured output is expected.
```

Prompt evaluation:

```text
DEFERRED_TO_PHASE_23 or AI quality phase.
```

---

## 7.7 AIG-007 — Event-triggered AI config

| Item | Value |
|---|---|
| Future capability | Configure AI agent to execute when registered event occurs |
| Current state | AiEventConfig exists |
| Phase 07 target | Harden EventRegistry binding and activation rules |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required fields:

```text
eventDefinitionId
agentId
promptVersionId
modelDeploymentId
environment
status
conditionJson optional
inputMappingJson optional
outputMappingJson optional
rateLimitPolicyId optional
createdAt/updatedAt
```

Required rules:

```text
1. EventDefinition must exist and be ACTIVE.
2. Agent must exist and be ACTIVE.
3. PromptVersion must be ACTIVE.
4. ModelDeployment must be ACTIVE.
5. Environment must match deployment environment.
6. Only one ACTIVE config per eventDefinition + environment + agent if current rule requires uniqueness.
7. EventConfig with DEPRECATED/INACTIVE EventDefinition cannot activate.
8. Activation audited.
9. Deactivation blocks new executions.
```

Condition/mapping engine:

```text
Simple pass-through now.
Advanced condition evaluation — DEFERRED_TO_PHASE_32_WORKFLOW_AUTOMATION.
```

---

## 7.8 AIG-008 — Manual AI execution

| Item | Value |
|---|---|
| Future capability | User manually runs an agent/prompt with supplied input |
| Current state | Execute event exists; manual execution not confirmed |
| Phase 07 target | Optional minimal API if current module supports it |
| Classification | `DEFERRED_TO_PHASE_21` unless current AI module already exposes it |

Potential API:

```text
POST /api/ai-agent/agents/{agentId}/execute
```

Rules if implemented:

```text
1. User must have AI_AGENT_EXECUTE.
2. Agent active.
3. Deployment active.
4. Prompt version active.
5. UsagePolicy allows execution.
6. Execution logged.
7. No business mutation.
```

---

## 7.9 AIG-009 — Execute event flow

| Item | Value |
|---|---|
| Future capability | Execute configured AI agent against a registered event payload |
| Current state | Execute event likely exists |
| Phase 07 target | Harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required API / internal handler:

```text
POST /api/ai-agent/events/execute
```

or internal consumer from platform outbox.

Rules:

```text
1. EventDefinition resolved by eventDefinitionId, eventCode, or sourceSystem+eventKey.
2. EventDefinition must be ACTIVE.
3. Active AiEventConfig must exist for event/environment.
4. AiUsagePolicy must allow execution.
5. ModelDeployment active.
6. PromptVersion active.
7. Input payload validated against EventDefinition schema where possible.
8. Prompt rendered with safe variables.
9. Provider client called through abstraction.
10. ExecutionLog created before/after call according to current reliability pattern.
11. Status SUCCEEDED/FAILED/BLOCKED recorded.
12. traceId propagated.
13. Errors do not leak provider secrets.
14. Output stored according to retention/sensitivity rule.
```

If external provider call is not implemented in current MVP:

```text
Mock/local execution may exist only under DEV profile.
Production must not fake success.
```

---

## 7.10 AIG-010 — Execution log

| Item | Value |
|---|---|
| Future capability | Full trace of AI executions for audit, debugging, billing, evaluation |
| Current state | AiExecutionLog exists |
| Phase 07 target | Harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required fields:

```text
id
agentId
eventDefinitionId optional
eventConfigId optional
promptTemplateId
promptVersionId
modelDeploymentId
providerId
modelId
environment
triggerType
triggeredByUserId optional
status
inputHash
inputPreviewJson optional redacted
outputPreviewJson optional redacted
promptTokens
completionTokens
totalTokens
estimatedCost
currency
startedAt
completedAt
durationMs
errorCode
errorMessage
traceId
createdAt
```

Status:

```text
PENDING
RUNNING
SUCCEEDED
FAILED
BLOCKED
CANCELLED
```

Trigger type:

```text
EVENT
MANUAL
SYSTEM_JOB
WORKFLOW future
```

Rules:

```text
1. Execution log must exist for every attempted execution.
2. Blocked by policy still logs BLOCKED.
3. Failed provider call logs FAILED.
4. Secrets not logged.
5. Prompt/output storage obeys sensitivity policy.
6. Execution log query requires AI_AGENT_VIEW or admin permission.
```

---

## 7.11 AIG-011 — Usage policy

| Item | Value |
|---|---|
| Future capability | Prevent runaway usage, unsafe model use, environment misuse, cost explosion |
| Current state | AiUsagePolicy exists |
| Phase 07 target | Harden core rules |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` |

Required policy dimensions:

```text
environment
agentId optional
modelDeploymentId optional
maxRequestsPerMinute optional
maxRequestsPerDay optional
maxTokensPerRequest optional
maxTokensPerDay optional
maxEstimatedCostPerDay optional
allowedEventDefinitionIds optional
blockedEventDefinitionIds optional
status
```

Rules:

```text
1. Only ACTIVE policy applies.
2. Execution blocked if policy denies event/agent/deployment.
3. Execution blocked if rate/token/cost limit exceeded.
4. Block creates execution log status BLOCKED.
5. Block emits AI_USAGE_POLICY_BLOCKED event.
6. Policy changes audited.
```

If cost tracking not implemented:

```text
Token/rate limit required; cost limit can be DEFERRED_TO_PHASE_22/23.
```

---

## 7.12 AIG-012 — Tool execution

| Item | Value |
|---|---|
| Future capability | AI can call tools/actions under governance |
| Current state | Not implemented |
| Phase 07 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21_AI_ASSISTED_PROJECT_PLANNING` and `POST_23_INTEGRATION_BACKLOG` |

Future entities:

```text
AiTool
AiToolPermission
AiToolExecution
AiAgentToolBinding
```

Future rules:

```text
1. Tool must be registered.
2. Agent must be allowed to use tool.
3. Requesting user must have target business permission.
4. Tool call must be logged.
5. Human approval required for mutation unless policy allows.
```

Do not implement fake tool execution now.

---

## 7.13 AIG-013 — Acting-on-behalf-of authorization

| Item | Value |
|---|---|
| Future capability | AI acts with both user and agent permissions |
| Current state | Not implemented |
| Phase 07 target | Defer but reserve principle |
| Classification | `DEFERRED_TO_PHASE_21` |

Future rule:

```text
Action allowed only if:
requesting user has business permission
AND agent identity has tool/resource permission
AND action is within usage/autonomy policy
AND approval state allows it.
```

Phase 07 must state:

```text
No direct business mutation by AI.
```

---

## 7.14 AIG-014 — AI suggestion review

| Item | Value |
|---|---|
| Future capability | Store AI-generated suggestions and allow human accept/reject |
| Current state | Not implemented |
| Phase 07 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21` |

Future entities:

```text
AiSuggestion
AiSuggestionItem
AiSuggestionDecision
```

Future events:

```text
AI_SUGGESTION_CREATED
AI_SUGGESTION_ACCEPTED
AI_SUGGESTION_REJECTED
```

---

## 7.15 AIG-015 — RAG / document context

| Item | Value |
|---|---|
| Future capability | AI reads knowledge/document context respecting IAM |
| Current state | Knowledge module only has DocumentType catalog; no full Document Hub/RAG |
| Phase 07 target | Defer |
| Classification | `DEFERRED_TO_PHASE_08_DOCUMENT_KNOWLEDGE_HUB` and `DEFERRED_TO_PHASE_21` |

Future entities:

```text
KnowledgeSource
Document
DocumentChunk
EmbeddingIndex
AiContextSource
AiContextRetrievalLog
```

Rules:

```text
1. AI retrieval must respect requesting user's effective access.
2. Document classification enforced.
3. Prompt logs must not store full confidential document text unless allowed.
4. Retrieved context IDs logged for audit.
```

---

## 7.16 AIG-016 — AI cost and usage reporting

| Item | Value |
|---|---|
| Future capability | Track usage by org/workspace/project/agent/provider/model |
| Current state | Execution log may have token/cost fields |
| Phase 07 target | Store token/cost fields if available; reporting deferred |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` for fields/logging; `DEFERRED_TO_PHASE_22_REPORTING` for dashboards |

Required now:

```text
promptTokens
completionTokens
totalTokens
estimatedCost
currency
```

If provider does not return cost:

```text
cost may be null
tokens may be null
```

Do not fake cost.

---

## 7.17 AIG-017 — Model/prompt evaluation

| Item | Value |
|---|---|
| Future capability | Evaluate prompt/model quality with test cases and feedback |
| Current state | Not implemented |
| Phase 07 target | Defer |
| Classification | `DEFERRED_TO_PHASE_23_CORE_HARDENING` or AI quality backlog |

Future entities:

```text
AiEvaluationDataset
AiEvaluationRun
AiEvaluationResult
AiHumanFeedback
```

---

## 7.18 AIG-018 — AI safety policy

| Item | Value |
|---|---|
| Future capability | Block unsafe or unauthorized AI requests |
| Current state | AiUsagePolicy exists; full safety not implemented |
| Phase 07 target | Implement usage policy foundation; content safety provider deferred |
| Classification | `MUST_IMPLEMENT_IN_PHASE_07` for usage policy; `DEFERRED_TO_PHASE_23` for advanced safety |

Rules now:

```text
1. Block inactive agent/deployment/prompt.
2. Block unauthorized user.
3. Block usage policy violations.
4. Block deprecated event configs.
5. Log BLOCKED execution.
```

Future:

```text
content moderation
PII redaction
prompt injection detection
RAG exfiltration control
```

---

# 8. Entity model TO-BE

If current schema differs, agent must map and document.

---

## 8.1 AiProvider — `ai_provider`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
provider_type VARCHAR(100) NOT NULL
base_url TEXT NULL
credential_ref VARCHAR(255) NULL
credential_encrypted TEXT NULL if local encrypted storage is used
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
deactivated_at / deactivated_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
DEACTIVATED
ARCHIVED
```

Rules:

```text
credential_ref preferred over credential_encrypted if external secret manager exists.
API responses never return credential_encrypted.
```

---

## 8.2 AiModel — `ai_model`

Required fields:

```text
id UUID PK
provider_id UUID NOT NULL
code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
model_family VARCHAR(100) NULL
capabilities_json JSONB NULL
context_window_tokens INT NULL
max_output_tokens INT NULL
status VARCHAR(50) NOT NULL
created_at / updated_at
version INT
```

Constraint:

```text
unique provider_id + code
```

---

## 8.3 AiModelDeployment — `ai_model_deployment`

Required fields:

```text
id UUID PK
provider_id UUID NOT NULL
model_id UUID NOT NULL
code VARCHAR(150) NOT NULL
environment VARCHAR(50) NOT NULL
deployment_name VARCHAR(255) NOT NULL
endpoint_url TEXT NULL
provider_model_name VARCHAR(255) NULL
config_json JSONB NULL
is_default BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / updated_at
activated_at / activated_by
deactivated_at / deactivated_by
version INT
```

Constraint:

```text
unique environment + code
optional unique default per environment/capability
```

---

## 8.4 AiAgent — `ai_agent`

Required fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
description TEXT NULL
agent_type VARCHAR(100) NOT NULL
scope VARCHAR(50) NOT NULL
organization_id UUID NULL
workspace_id UUID NULL
default_deployment_id UUID NULL
autonomy_level VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Scope:

```text
SYSTEM
ORGANIZATION
WORKSPACE
```

Phase 07 minimum:

```text
SYSTEM agents only if org/workspace scoped agents are not implemented.
```

---

## 8.5 AiPromptTemplate — `ai_prompt_template`

Required fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
description TEXT NULL
agent_id UUID NULL
scope VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
version INT
```

---

## 8.6 AiPromptVersion — `ai_prompt_version`

Required fields:

```text
id UUID PK
prompt_template_id UUID NOT NULL
version_number INT NOT NULL
system_prompt TEXT NULL
user_prompt_template TEXT NOT NULL
response_format VARCHAR(100) NULL
response_schema_json JSONB NULL
temperature DECIMAL(5,2) NULL
top_p DECIMAL(5,2) NULL
max_tokens INT NULL
status VARCHAR(50) NOT NULL
activated_at TIMESTAMP NULL
activated_by UUID NULL
created_at / created_by
updated_at / updated_by
```

Constraint:

```text
unique prompt_template_id + version_number
```

Status:

```text
DRAFT
ACTIVE
ARCHIVED
```

---

## 8.7 AiEventConfig — `ai_event_config`

Required fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL UNIQUE
event_definition_id UUID NOT NULL
agent_id UUID NOT NULL
prompt_version_id UUID NOT NULL
model_deployment_id UUID NOT NULL
environment VARCHAR(50) NOT NULL
condition_json JSONB NULL
input_mapping_json JSONB NULL
output_mapping_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
activated_at / activated_by
deactivated_at / deactivated_by
version INT
```

Constraint:

```text
unique event_definition_id + agent_id + environment where status ACTIVE
```

If current uniqueness differs, document.

---

## 8.8 AiExecutionLog — `ai_execution_log`

Required fields:

```text
id UUID PK
event_config_id UUID NULL
event_definition_id UUID NULL
agent_id UUID NOT NULL
prompt_template_id UUID NULL
prompt_version_id UUID NOT NULL
model_deployment_id UUID NOT NULL
provider_id UUID NULL
model_id UUID NULL
environment VARCHAR(50) NOT NULL
trigger_type VARCHAR(50) NOT NULL
triggered_by_user_id UUID NULL
status VARCHAR(50) NOT NULL
input_hash VARCHAR(255) NULL
input_preview_json JSONB NULL
output_preview_json JSONB NULL
prompt_tokens INT NULL
completion_tokens INT NULL
total_tokens INT NULL
estimated_cost DECIMAL(18,6) NULL
currency VARCHAR(10) NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
duration_ms BIGINT NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
trace_id VARCHAR(100) NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

---

## 8.9 AiUsagePolicy — `ai_usage_policy`

Required fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
environment VARCHAR(50) NULL
agent_id UUID NULL
model_deployment_id UUID NULL
max_requests_per_minute INT NULL
max_requests_per_day INT NULL
max_tokens_per_request INT NULL
max_tokens_per_day INT NULL
max_estimated_cost_per_day DECIMAL(18,6) NULL
allowed_event_definition_ids JSONB NULL
blocked_event_definition_ids JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
activated_at / activated_by
deactivated_at / deactivated_by
version INT
```

Status:

```text
DRAFT
ACTIVE
INACTIVE
ARCHIVED
```

---

# 9. API TO-BE list

All APIs use `/api`.

---

## 9.1 Provider APIs

```text
POST  /api/ai-agent/providers
GET   /api/ai-agent/providers
GET   /api/ai-agent/providers/{id}
PUT   /api/ai-agent/providers/{id}
PATCH /api/ai-agent/providers/{id}/activate
PATCH /api/ai-agent/providers/{id}/deactivate
PATCH /api/ai-agent/providers/{id}/archive
```

Rules:

```text
Provider secret fields write-only.
GET never returns API key/secret.
```

---

## 9.2 Model APIs

```text
POST  /api/ai-agent/models
GET   /api/ai-agent/models
GET   /api/ai-agent/models/{id}
PUT   /api/ai-agent/models/{id}
PATCH /api/ai-agent/models/{id}/activate
PATCH /api/ai-agent/models/{id}/deactivate
```

---

## 9.3 Deployment APIs

```text
POST  /api/ai-agent/deployments
GET   /api/ai-agent/deployments
GET   /api/ai-agent/deployments/{id}
PUT   /api/ai-agent/deployments/{id}
PATCH /api/ai-agent/deployments/{id}/activate
PATCH /api/ai-agent/deployments/{id}/deactivate
PATCH /api/ai-agent/deployments/{id}/set-default
```

---

## 9.4 Agent APIs

```text
POST  /api/ai-agent/agents
GET   /api/ai-agent/agents
GET   /api/ai-agent/agents/{id}
PUT   /api/ai-agent/agents/{id}
PATCH /api/ai-agent/agents/{id}/activate
PATCH /api/ai-agent/agents/{id}/deactivate
PATCH /api/ai-agent/agents/{id}/archive
```

---

## 9.5 Prompt template/version APIs

```text
POST  /api/ai-agent/prompt-templates
GET   /api/ai-agent/prompt-templates
GET   /api/ai-agent/prompt-templates/{id}
PUT   /api/ai-agent/prompt-templates/{id}
PATCH /api/ai-agent/prompt-templates/{id}/activate
PATCH /api/ai-agent/prompt-templates/{id}/archive

POST  /api/ai-agent/prompt-templates/{templateId}/versions
GET   /api/ai-agent/prompt-templates/{templateId}/versions
GET   /api/ai-agent/prompt-templates/{templateId}/versions/{versionId}
PUT   /api/ai-agent/prompt-templates/{templateId}/versions/{versionId}
POST  /api/ai-agent/prompt-templates/{templateId}/versions/{versionId}/activate
POST  /api/ai-agent/prompt-templates/{templateId}/versions/{versionId}/preview
```

---

## 9.6 Event config APIs

```text
POST  /api/ai-agent/event-configs
GET   /api/ai-agent/event-configs
GET   /api/ai-agent/event-configs/{id}
PUT   /api/ai-agent/event-configs/{id}
PATCH /api/ai-agent/event-configs/{id}/activate
PATCH /api/ai-agent/event-configs/{id}/deactivate
```

---

## 9.7 Execute event APIs

```text
POST /api/ai-agent/events/execute
```

Optional internal only:

```text
AiEventExecutionService.execute(EventEnvelope)
```

If public API exists:

```text
Requires AI_AGENT_EXECUTE_EVENT or internal service token.
```

Do not expose arbitrary execution publicly without IAM.

---

## 9.8 Execution log APIs

```text
GET /api/ai-agent/executions
GET /api/ai-agent/executions/{id}
```

Filters:

```text
agentId
eventDefinitionId
status
environment
dateFrom/dateTo
workspaceId/projectId future
```

---

## 9.9 Usage policy APIs

```text
POST  /api/ai-agent/usage-policies
GET   /api/ai-agent/usage-policies
GET   /api/ai-agent/usage-policies/{id}
PUT   /api/ai-agent/usage-policies/{id}
PATCH /api/ai-agent/usage-policies/{id}/activate
PATCH /api/ai-agent/usage-policies/{id}/deactivate
PATCH /api/ai-agent/usage-policies/{id}/archive
```

---

# 10. Authorization requirements

Required IAM authorities:

```text
AI_PROVIDER_VIEW
AI_PROVIDER_CREATE
AI_PROVIDER_UPDATE
AI_PROVIDER_MANAGE

AI_MODEL_VIEW
AI_MODEL_CREATE
AI_MODEL_UPDATE
AI_MODEL_MANAGE

AI_DEPLOYMENT_VIEW
AI_DEPLOYMENT_CREATE
AI_DEPLOYMENT_UPDATE
AI_DEPLOYMENT_MANAGE

AI_AGENT_VIEW
AI_AGENT_CREATE
AI_AGENT_UPDATE
AI_AGENT_MANAGE

AI_PROMPT_VIEW
AI_PROMPT_CREATE
AI_PROMPT_UPDATE
AI_PROMPT_ACTIVATE
AI_PROMPT_MANAGE

AI_EVENT_CONFIG_VIEW
AI_EVENT_CONFIG_CREATE
AI_EVENT_CONFIG_UPDATE
AI_EVENT_CONFIG_ACTIVATE
AI_EVENT_CONFIG_MANAGE

AI_EXECUTION_VIEW
AI_EXECUTION_EXECUTE
AI_EXECUTION_MANAGE

AI_USAGE_POLICY_VIEW
AI_USAGE_POLICY_CREATE
AI_USAGE_POLICY_UPDATE
AI_USAGE_POLICY_MANAGE
```

Rules:

```text
1. Provider/model/deployment admin endpoints require system/admin AI permission.
2. Prompt/template changes require AI prompt permission.
3. Activating prompt/event config/policy requires manage/activate permission.
4. Execution log read requires AI_EXECUTION_VIEW.
5. Execute event requires internal service or AI_EXECUTION_EXECUTE.
6. Workspace-scoped agents must require workspace access if implemented.
7. System seeders can use internal service path, not user auth.
```

Future:

```text
AI_ACT_ON_BEHALF — Phase 21
AI_TOOL_EXECUTE — Phase 21
AI_CONTEXT_READ — Phase 21/08
```

---

# 11. Event Registry integration

Phase 07 must follow Phase 05.

Rules:

```text
1. AiEventConfig must reference EventDefinition.
2. EventDefinition must be ACTIVE to activate config.
3. Deprecated EventDefinition blocks new activation.
4. ExecuteEvent rejects unknown event.
5. ExecuteEvent rejects inactive/deprecated event unless compatibility mode is explicit.
6. Prompt variable mapping should use EventVariable where event-bound.
7. Event config deactivation should not delete execution history.
```

AI event config should not store raw event string only.

---

# 12. Notification integration

Phase 07 must seed or support notifications for AI failures/policy blocks.

Required event definitions:

```text
AI_EXECUTION_FAILED
AI_USAGE_POLICY_BLOCKED
AI_EVENT_CONFIG_ACTIVATED
```

Optional notification templates:

```text
AI_EXECUTION_FAILED_ADMIN_EMAIL
AI_USAGE_POLICY_BLOCKED_ADMIN_EMAIL
```

Classification:

```text
SEED_ONLY_IN_PHASE_07 unless notification routing exists.
```

Rules:

```text
1. Do not spam end users for every AI failure.
2. Notify admins/support only for repeated failures or critical configs.
3. Policy block events should be auditable.
```

Full project-facing AI notifications:

```text
DEFERRED_TO_PHASE_21
```

---

# 13. Platform audit/outbox/idempotency integration

Phase 07 must use Phase 04 patterns.

## 13.1 Activity log

Required actions:

```text
AI_PROVIDER_CREATED
AI_PROVIDER_UPDATED
AI_PROVIDER_DEACTIVATED
AI_MODEL_CREATED
AI_MODEL_UPDATED
AI_DEPLOYMENT_CREATED
AI_DEPLOYMENT_ACTIVATED
AI_DEPLOYMENT_DEACTIVATED
AI_AGENT_CREATED
AI_AGENT_UPDATED
AI_AGENT_ACTIVATED
AI_AGENT_DEACTIVATED
AI_PROMPT_TEMPLATE_CREATED
AI_PROMPT_VERSION_CREATED
AI_PROMPT_VERSION_ACTIVATED
AI_EVENT_CONFIG_CREATED
AI_EVENT_CONFIG_ACTIVATED
AI_EVENT_CONFIG_DEACTIVATED
AI_USAGE_POLICY_CREATED
AI_USAGE_POLICY_ACTIVATED
AI_EXECUTION_STARTED
AI_EXECUTION_SUCCEEDED
AI_EXECUTION_FAILED
AI_EXECUTION_BLOCKED
```

## 13.2 Audit

Audit-sensitive actions:

```text
AI_PROVIDER_SECRET_UPDATED
AI_DEPLOYMENT_ACTIVATED
AI_PROMPT_VERSION_ACTIVATED
AI_EVENT_CONFIG_ACTIVATED
AI_USAGE_POLICY_CHANGED
AI_USAGE_POLICY_BLOCKED
AI_EXECUTION_FAILED
AI_AGENT_DEACTIVATED
```

## 13.3 Outbox events

AI module should emit registered events through platform outbox if available.

If platform outbox is not generic yet:

```text
Use existing application event mechanism and document gap.
```

## 13.4 Idempotency

Idempotency recommended for:

```text
POST /api/ai-agent/providers
POST /api/ai-agent/models
POST /api/ai-agent/deployments
POST /api/ai-agent/agents
POST /api/ai-agent/prompt-templates
POST /api/ai-agent/prompt-templates/{id}/versions
POST /api/ai-agent/event-configs
POST /api/ai-agent/events/execute
```

Rules:

```text
Same Idempotency-Key should not create duplicate provider/model/deployment/agent/config/execution.
```

Execution idempotency:

```text
For same event occurrence and same config, duplicate execution should be blocked or return existing log.
```

---

# 14. Secret handling rules

AI provider secrets are highly sensitive.

Rules:

```text
1. API key/secret must be write-only.
2. API response returns masked indicator only, e.g. hasCredential=true.
3. Secrets stored encrypted or referenced externally.
4. Secret updates audited.
5. Secret values never appear in activity/audit/outbox/execution logs.
6. Provider client logs must redact headers.
7. Error messages from provider must be sanitized.
8. Test must assert response does not include credential.
```

If current code stores secrets plaintext:

```text
MUST_IMPLEMENT_IN_PHASE_07 migration or document severe risk and defer only with explicit owner approval.
```

---

# 15. Prompt safety rules

Phase 07 does not implement full AI safety, but must enforce foundation.

Rules:

```text
1. PromptVersion ACTIVE is immutable.
2. Prompt execution stores promptVersionId.
3. Prompt output is not automatically applied to business data.
4. Prompt variables from events are validated/mapped.
5. Prompt logs are redacted if sensitive.
6. Prompt preview should require permission.
7. System prompt changes require audit.
8. Prompt activation requires explicit permission.
```

Future:

```text
Prompt injection detection — DEFERRED_TO_PHASE_23.
Prompt evaluation datasets — DEFERRED_TO_PHASE_23.
Human feedback — DEFERRED_TO_PHASE_21/23.
```

---

# 16. Execution safety rules

AI execution can be expensive and sensitive.

Rules:

```text
1. Execution allowed only if Agent ACTIVE.
2. Deployment ACTIVE.
3. PromptVersion ACTIVE.
4. EventConfig ACTIVE for event-triggered execution.
5. UsagePolicy allows it.
6. Execution log created for success/failure/block.
7. Provider timeout configured.
8. Provider errors sanitized.
9. No unbounded prompt length.
10. maxTokens enforced if configured.
11. No automatic business mutation.
```

Timeout/retry:

```text
Do not automatically retry non-idempotent expensive model calls unless policy allows.
```

Recommended:

```text
Fail fast and log.
Manual retry future.
```

---

# 17. AI usage policy rules

## 17.1 Policy evaluation order

```text
1. Global/environment policy
2. Deployment policy
3. Agent policy
4. Event-specific allow/block list
5. Request token limit
6. Rate limit
7. Daily token/cost limit if available
```

## 17.2 Blocking behavior

When blocked:

```text
1. No provider call.
2. ExecutionLog status = BLOCKED.
3. reasonCode stored.
4. AI_USAGE_POLICY_BLOCKED event emitted.
5. Audit event created.
```

Reason codes:

```text
POLICY_INACTIVE
AGENT_NOT_ALLOWED
DEPLOYMENT_NOT_ALLOWED
EVENT_BLOCKED
RATE_LIMIT_EXCEEDED
TOKEN_LIMIT_EXCEEDED
COST_LIMIT_EXCEEDED
ENVIRONMENT_NOT_ALLOWED
```

---

# 18. Event definitions owned by AI Agent

Source system:

```text
SCOPERY_AI_AGENT
```

Required Phase 07 event definitions:

```text
AI_PROVIDER_CREATED
AI_PROVIDER_UPDATED
AI_PROVIDER_DEACTIVATED
AI_MODEL_CREATED
AI_MODEL_UPDATED
AI_MODEL_DEACTIVATED
AI_DEPLOYMENT_CREATED
AI_DEPLOYMENT_ACTIVATED
AI_DEPLOYMENT_DEACTIVATED
AI_AGENT_CREATED
AI_AGENT_UPDATED
AI_AGENT_ACTIVATED
AI_AGENT_DEACTIVATED
AI_PROMPT_TEMPLATE_CREATED
AI_PROMPT_VERSION_CREATED
AI_PROMPT_VERSION_ACTIVATED
AI_EVENT_CONFIG_CREATED
AI_EVENT_CONFIG_ACTIVATED
AI_EVENT_CONFIG_DEACTIVATED
AI_EXECUTION_STARTED
AI_EXECUTION_SUCCEEDED
AI_EXECUTION_FAILED
AI_USAGE_POLICY_CREATED
AI_USAGE_POLICY_ACTIVATED
AI_USAGE_POLICY_BLOCKED
```

Future Phase 21 events:

```text
AI_PROJECT_PLAN_SUGGESTED
AI_PROJECT_PLAN_APPLIED
AI_PROJECT_PLAN_REJECTED
AI_ACTION_BLOCKED_BY_IAM
AI_TOOL_EXECUTION_STARTED
AI_TOOL_EXECUTION_SUCCEEDED
AI_TOOL_EXECUTION_FAILED
```

Future Document/RAG events:

```text
AI_CONTEXT_RETRIEVED
AI_CONTEXT_ACCESS_DENIED
AI_DOCUMENT_INDEXING_REQUESTED
AI_DOCUMENT_INDEXING_COMPLETED
```

---

# 19. Seeder requirements

## 19.1 AI event seeder

Must seed required AI event definitions and variables.

Rules:

```text
1. Idempotent.
2. Does not duplicate events.
3. Does not delete active event variables.
4. Data classification correct.
5. Sensitive variables marked.
```

## 19.2 Default usage policy seeder

Recommended seed:

```text
DEFAULT_AI_USAGE_POLICY_DEV
DEFAULT_AI_USAGE_POLICY_PROD
```

Rules:

```text
1. Conservative limits.
2. Prod policy stricter.
3. Does not enable execution if no deployment/provider configured.
4. Idempotent.
```

If not seeding policies:

```text
Document that AI execution requires manually created UsagePolicy.
```

## 19.3 Default agent/prompt seeder

Only seed default agents/prompts if product wants out-of-box AI.

Possible seed-only but inactive:

```text
SYSTEM_EVENT_SUMMARIZER_AGENT
PROJECT_PLANNING_ASSISTANT_AGENT inactive until Phase 21
```

Recommended:

```text
Do not seed active business agents before their business modules are ready.
```

---

# 20. Business rules master

## 20.1 Provider rules

```text
AI-PROV-001 Provider code unique.
AI-PROV-002 Provider name required.
AI-PROV-003 Provider type required.
AI-PROV-004 Provider secret write-only.
AI-PROV-005 Provider secret never returned.
AI-PROV-006 Provider deactivate blocked if active deployments exist unless forced.
AI-PROV-007 Inactive provider cannot execute.
```

## 20.2 Model rules

```text
AI-MODEL-001 Model provider must exist.
AI-MODEL-002 Provider must be active for model activation.
AI-MODEL-003 Model code unique per provider.
AI-MODEL-004 Inactive model cannot be deployed.
AI-MODEL-005 Model deactivate blocked if active deployments exist unless forced.
```

## 20.3 Deployment rules

```text
AI-DEP-001 Deployment references active provider/model.
AI-DEP-002 Deployment environment required.
AI-DEP-003 Deployment code unique per environment.
AI-DEP-004 Only ACTIVE deployment can execute.
AI-DEP-005 Deactivation blocks execution.
AI-DEP-006 Default deployment uniqueness enforced if supported.
AI-DEP-007 Deployment config must not expose secret.
```

## 20.4 Agent rules

```text
AI-AGT-001 Agent code unique.
AI-AGT-002 Agent name required.
AI-AGT-003 Agent type required.
AI-AGT-004 Agent status controls execution.
AI-AGT-005 Agent autonomy level required.
AI-AGT-006 Phase 07 agent cannot mutate business data.
```

## 20.5 Prompt rules

```text
AI-PRM-001 Prompt template code unique.
AI-PRM-002 Version number unique per template.
AI-PRM-003 DRAFT prompt version can be edited.
AI-PRM-004 ACTIVE prompt version immutable.
AI-PRM-005 Activation requires permission.
AI-PRM-006 Execution log stores promptVersionId.
AI-PRM-007 Prompt variables validated when event-bound.
```

## 20.6 EventConfig rules

```text
AI-EVC-001 EventDefinition must be ACTIVE.
AI-EVC-002 Agent must be ACTIVE to activate config.
AI-EVC-003 PromptVersion must be ACTIVE.
AI-EVC-004 ModelDeployment must be ACTIVE.
AI-EVC-005 Environment must match deployment.
AI-EVC-006 Inactive config cannot execute.
AI-EVC-007 Duplicate active config blocked by uniqueness rule.
AI-EVC-008 Activation audited.
```

## 20.7 Execution rules

```text
AI-EXE-001 Unknown event rejected.
AI-EXE-002 Inactive/deprecated event rejected.
AI-EXE-003 No active config means no execution.
AI-EXE-004 UsagePolicy checked before provider call.
AI-EXE-005 Policy block logs BLOCKED and emits event.
AI-EXE-006 Provider error logs FAILED.
AI-EXE-007 Success logs SUCCEEDED.
AI-EXE-008 traceId propagated.
AI-EXE-009 Secrets never logged.
AI-EXE-010 No business mutation in Phase 07.
```

---

# 21. Error catalog requirements

Exact names follow project convention, but concepts must exist.

```text
AI_PROVIDER_NOT_FOUND
AI_PROVIDER_CODE_ALREADY_EXISTS
AI_PROVIDER_INACTIVE
AI_PROVIDER_HAS_ACTIVE_DEPLOYMENTS
AI_PROVIDER_SECRET_REQUIRED
AI_PROVIDER_SECRET_NOT_RETURNABLE

AI_MODEL_NOT_FOUND
AI_MODEL_CODE_ALREADY_EXISTS
AI_MODEL_INACTIVE
AI_MODEL_HAS_ACTIVE_DEPLOYMENTS

AI_DEPLOYMENT_NOT_FOUND
AI_DEPLOYMENT_CODE_ALREADY_EXISTS
AI_DEPLOYMENT_INACTIVE
AI_DEPLOYMENT_ENVIRONMENT_MISMATCH

AI_AGENT_NOT_FOUND
AI_AGENT_CODE_ALREADY_EXISTS
AI_AGENT_INACTIVE
AI_AGENT_AUTONOMY_NOT_ALLOWED

AI_PROMPT_TEMPLATE_NOT_FOUND
AI_PROMPT_TEMPLATE_CODE_ALREADY_EXISTS
AI_PROMPT_VERSION_NOT_FOUND
AI_PROMPT_VERSION_NOT_DRAFT
AI_PROMPT_VERSION_ALREADY_ACTIVE
AI_PROMPT_VARIABLE_UNKNOWN
AI_PROMPT_RESPONSE_SCHEMA_INVALID

AI_EVENT_CONFIG_NOT_FOUND
AI_EVENT_CONFIG_ALREADY_ACTIVE
AI_EVENT_CONFIG_EVENT_INACTIVE
AI_EVENT_CONFIG_AGENT_INACTIVE
AI_EVENT_CONFIG_PROMPT_INACTIVE
AI_EVENT_CONFIG_DEPLOYMENT_INACTIVE
AI_EVENT_CONFIG_ENVIRONMENT_MISMATCH

AI_EXECUTION_NO_ACTIVE_CONFIG
AI_EXECUTION_POLICY_BLOCKED
AI_EXECUTION_PROVIDER_FAILED
AI_EXECUTION_TIMEOUT
AI_EXECUTION_INVALID_INPUT

AI_USAGE_POLICY_NOT_FOUND
AI_USAGE_POLICY_CODE_ALREADY_EXISTS
AI_USAGE_POLICY_LIMIT_EXCEEDED
```

---

# 22. Required tests

Phase 07 is incomplete without tests.

---

## 22.1 Provider tests

```text
createProvider_valid_success
createProvider_duplicateCode_conflict
createProvider_missingName_validation
createProvider_secretWriteOnly_notReturned
updateProviderSecret_audited
deactivateProvider_withActiveDeployment_rejected
deactivateProvider_withoutActiveDeployment_success
```

## 22.2 Model tests

```text
createModel_valid_success
createModel_providerNotFound_rejected
createModel_inactiveProvider_rejected
createModel_duplicateCodePerProvider_conflict
deactivateModel_withActiveDeployment_rejected
```

## 22.3 Deployment tests

```text
createDeployment_valid_success
createDeployment_inactiveProvider_rejected
createDeployment_inactiveModel_rejected
createDeployment_duplicateCodeEnvironment_conflict
activateDeployment_valid_success
deactivateDeployment_blocksExecution
setDefaultDeployment_uniquePerEnvironment
deploymentResponse_doesNotExposeSecret
```

## 22.4 Agent tests

```text
createAgent_valid_success
createAgent_duplicateCode_conflict
activateAgent_valid_success
deactivateAgent_blocksExecution
agentAutonomy_mutationLevelRejectedInPhase07
```

## 22.5 Prompt template/version tests

```text
createPromptTemplate_valid_success
createPromptTemplate_duplicateCode_conflict
createPromptVersion_valid_success
updateDraftPromptVersion_success
updateActivePromptVersion_rejected
activatePromptVersion_valid_success
activatePromptVersion_deactivatesPreviousActive_ifSingleActive
promptPreview_valid_success
promptPreview_unknownEventVariable_rejected
```

## 22.6 EventConfig tests

```text
createEventConfig_valid_success
createEventConfig_eventNotFound_rejected
createEventConfig_inactiveEvent_rejected
createEventConfig_deprecatedEvent_rejected
createEventConfig_inactiveAgent_rejected
createEventConfig_inactivePromptVersion_rejected
createEventConfig_inactiveDeployment_rejected
createEventConfig_environmentMismatch_rejected
activateEventConfig_valid_success
duplicateActiveConfig_conflict
deactivateEventConfig_blocksExecution
```

## 22.7 Execute event tests

```text
executeEvent_registeredActiveEvent_success
executeEvent_unknownEvent_rejected
executeEvent_inactiveEvent_rejected
executeEvent_noActiveConfig_noExecution
executeEvent_policyBlocked_logsBlocked
executeEvent_policyBlocked_emitsEvent
executeEvent_providerFailure_logsFailed
executeEvent_success_logsSucceeded
executeEvent_traceIdPropagated
executeEvent_doesNotLogSecrets
executeEvent_doesNotMutateBusinessData
executeEvent_idempotencySameOccurrence_noDuplicateExecution
```

## 22.8 UsagePolicy tests

```text
createUsagePolicy_valid_success
createUsagePolicy_duplicateCode_conflict
activateUsagePolicy_valid_success
policyBlocksDisallowedEvent
policyBlocksRateLimit
policyBlocksTokenLimit
policyBlockCreatesAudit
```

## 22.9 Event seeder tests

```text
aiEventSeeder_firstRun_createsDefinitions
aiEventSeeder_secondRun_noDuplicates
aiEventSeeder_variablesCreated
aiEventSeeder_sensitiveVariablesMarked
```

## 22.10 Authorization tests

```text
createProvider_withoutPermission_forbidden
createAgent_withoutPermission_forbidden
activatePrompt_withoutPermission_forbidden
executeEvent_withoutPermissionOrInternalToken_forbidden
executionLog_withoutPermission_forbidden
```

---

# 23. Manual verification checklist

Completion file must include:

```text
1. Create AI provider with secret.
2. Verify GET provider masks secret.
3. Create AI model under provider.
4. Create deployment.
5. Activate deployment.
6. Create agent.
7. Create prompt template.
8. Create prompt version.
9. Activate prompt version.
10. Create event config for ACTIVE EventDefinition.
11. Try event config for inactive/deprecated event and confirm rejection.
12. Create usage policy.
13. Execute event successfully using safe/mock provider if configured.
14. Confirm execution log SUCCEEDED.
15. Force provider failure and confirm FAILED log.
16. Force usage policy block and confirm BLOCKED log.
17. Confirm AI_USAGE_POLICY_BLOCKED event emitted.
18. Confirm no secret appears in logs/API/outbox/execution.
19. Rerun AI event seeder and confirm no duplicates.
```

---

# 24. Acceptance criteria

Phase 07 is accepted only if:

```text
1. Current AI Agent module is classified against TO-BE.
2. Provider/model/deployment lifecycle implemented or verified.
3. Provider secret handling is safe and tested.
4. Agent lifecycle implemented or verified.
5. Prompt template/version lifecycle implemented or verified.
6. Active prompt versions are immutable.
7. EventConfig validates EventRegistry ACTIVE events.
8. ExecuteEvent logs every attempt.
9. UsagePolicy can block execution.
10. Policy block creates audit/log/event.
11. Execution does not mutate business data.
12. AI event definitions are seeded idempotently.
13. IAM authorization protects AI admin APIs.
14. TraceId propagates into execution log.
15. Tests added for implemented behavior.
16. Deferred AI planning/RAG/tools/acting-on-behalf are listed with phase owners.
17. mvn compile passes.
18. mvn test passes.
19. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
provider secrets are returned/logged
inactive deployment can execute
inactive/deprecated EventDefinition can activate EventConfig
active prompt version can be edited
usage policy block still calls provider
execution can mutate business data in Phase 07
AI project planning/RAG/tool execution is claimed implemented without real code
future gaps are not documented
```

---

# 25. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_07_AI_AGENT_PLATFORM_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 07 — AI Agent Platform TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Entity Mapping
## 9. API Changes
## 10. Provider Secret Strategy
## 11. Model / Deployment Strategy
## 12. Agent / Autonomy Strategy
## 13. Prompt Versioning Strategy
## 14. EventConfig / EventRegistry Integration
## 15. ExecuteEvent Flow
## 16. UsagePolicy Rules
## 17. Execution Log Contract
## 18. IAM Authorization Matrix
## 19. Event Seeder Matrix
## 20. Notification Integration
## 21. Activity / Audit / Outbox Notes
## 22. Tests Added
## 23. Commands Run
## 24. Test Results
## 25. Manual Verification
## 26. Assumptions
## 27. Deviations From Prompt
## 28. Known Risks
## 29. Future Phases That Must Return to AI Platform
```

---

# 26. Future phases that must return to AI Platform

## 26.1 Phase 08 — Knowledge / Document Hub

Must add:

```text
AI context source integration
document access-safe retrieval
document indexing events
embedding model/deployment support if not yet present
```

## 26.2 Phase 12–14 — Capacity / Scheduling / Gantt

May add AI schedule risk explanation:

```text
AI_SCHEDULE_RISK_ANALYZED
AI_CAPACITY_RECOMMENDATION_CREATED
```

## 26.3 Phase 17 — Finance

May add AI finance explanation:

```text
AI_MARGIN_EXPLANATION_CREATED
AI_COST_RISK_ANALYZED
```

Must enforce finance permission and masking.

## 26.4 Phase 18 — Quote

May add quote drafting assistant:

```text
AI_QUOTE_DRAFT_CREATED
AI_CONTRACT_VALUE_EXPLANATION_CREATED
```

Requires commercial approval controls.

## 26.5 Phase 19 — Baseline / Change Request

May add AI change impact analysis:

```text
AI_CHANGE_IMPACT_ANALYSIS_CREATED
```

## 26.6 Phase 20 — Project Notifications

AI events can generate user/admin notifications.

## 26.7 Phase 21 — AI-assisted Project Planning

This is the main AI business expansion.

Must implement:

```text
AI project plan suggestion
WBS suggestion
Task suggestion
Estimate suggestion
Human accept/reject
Acting-on-behalf authorization
Tool permission
Suggestion audit
Project mutation only after human approval
```

## 26.8 Phase 22 — Reporting

AI usage/cost/performance reports.

## 26.9 Phase 23 — Core hardening

Must add/harden:

```text
prompt injection controls
content safety provider
evaluation dataset
feedback loop
cost budgets
security testing
AI access review
```

## 26.10 Post-23 Integration backlog

May add:

```text
external AI provider webhooks
tool marketplace
custom tools
service accounts/API tokens
```

---

# 27. Agent anti-bịa rules

The agent must not:

```text
1. Treat current AI Agent 15 functions as full AI platform future-state.
2. Claim AI project planning is implemented in Phase 07.
3. Claim RAG/document AI exists if only DocumentType exists.
4. Claim AI tool execution exists without AiTool/AiToolExecution/IAM checks.
5. Claim acting-on-behalf exists without dual user+agent authorization.
6. Return or log provider secrets.
7. Allow inactive/deprecated event to activate EventConfig.
8. Allow inactive deployment/prompt/agent to execute.
9. Call provider after UsagePolicy blocks execution.
10. Store sensitive prompt/input/output unredacted without policy.
11. Mutate project/task/finance/quote data in Phase 07.
12. Hide deferred AI gaps.
```

---

# 28. Prompt to give coding agent

```text
You are implementing Phase 07 — TO-BE AI Agent Platform, Model Governance, Prompt Versioning, Event Config, Execution Log & Usage Policy.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 API/security baseline
- Phase 02 IAM TO-BE spec
- Phase 03 Workspace TO-BE spec
- Phase 04 Platform Audit/Outbox/Idempotency spec
- Phase 05 Event Registry TO-BE spec
- Phase 06 Notification TO-BE spec
- Current BE feature/entity/business-rule inventory
- Dynamic Work OS feature catalog
- Existing aiagent module code, migrations, seeders, tests
- Existing EventRegistry code
- Existing Notification module code
- Existing IAM permission model

Your task:
1. Compare current AI Agent module against this TO-BE Phase 07 spec.
2. Classify every AI capability as CURRENTLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_07, SEED_ONLY_IN_PHASE_07, DEFERRED_TO_PHASE_XX, DEFERRED_TO_POST_23_BACKLOG, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 07 required items.
4. Harden provider/model/deployment/agent/prompt/event config/execution log/usage policy.
5. Ensure provider secrets are write-only, encrypted/referenced, and never returned/logged.
6. Ensure EventConfig only binds ACTIVE EventDefinition.
7. Ensure execution uses active agent, prompt version, deployment, and usage policy.
8. Ensure every execution attempt logs SUCCEEDED/FAILED/BLOCKED.
9. Ensure UsagePolicy block prevents provider call.
10. Ensure no AI execution mutates business data in Phase 07.
11. Seed/verify AI EventDefinitions.
12. Add tests listed in Phase 07 spec.
13. Run mvn compile and mvn test.
14. Create docs/phase-complete/PHASE_07_AI_AGENT_PLATFORM_TO_BE_COMPLETE.md with full gap matrix.

Do not claim AI planning, RAG, tool execution, acting-on-behalf, or autonomous business mutation exists unless implemented and tested in the correct later phase.
```

---

# 29. Quick tracking matrix

| Capability | Current backend | Phase 07 action | Later phase |
|---|---|---|---|
| AiProvider | Present | Harden/test/secret safety | — |
| AiModel | Present | Harden/test | — |
| AiModelDeployment | Present | Harden/test | — |
| AiAgent | Present | Harden/test autonomy | — |
| AiPromptTemplate | Present | Harden/test | — |
| AiPromptVersion | Present | Harden immutability | — |
| AiEventConfig | Present | Harden EventRegistry binding | — |
| ExecuteEvent | Present | Harden logging/policy | — |
| AiExecutionLog | Present | Harden fields/redaction | Phase 22 for reporting |
| AiUsagePolicy | Present | Harden block/rate/token | Phase 23 for advanced cost |
| Provider secret encryption | Unknown | Must verify/implement | — |
| Tool execution | Missing | Defer | Phase 21 |
| Acting-on-behalf | Missing | Defer | Phase 21 |
| AI suggestions | Missing | Defer | Phase 21 |
| Project planning AI | Missing | Defer | Phase 21 |
| RAG/document context | Missing | Defer | Phase 08/21 |
| Embedding/vector index | Missing | Defer | Phase 08/21 |
| Prompt evaluation | Missing | Defer | Phase 23 |
| Content safety | Partial/missing | Defer advanced | Phase 23 |
| AI cost dashboard | Missing | Defer | Phase 22 |
| AI notifications | Partial/future | Seed/admin only | Phase 20/21 |

---

# 30. Final principle

Phase 07 is not complete when "an AI API can be called."

Phase 07 is complete when Scopery has a governed AI platform:

```text
approved providers
approved models
active deployments
controlled agents
versioned prompts
event configs bound to EventRegistry
usage policies
execution logs
secret safety
audit trail
no unauthorized business mutation
clear future AI expansion path
```

AI must be helpful, but never uncontrolled.
