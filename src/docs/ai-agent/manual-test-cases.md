# AI Agent — Manual Test Cases

These test cases cover the critical flows that cannot be covered by unit tests because they require a live database and real (or mocked) AI provider calls.

Run the demo seed first: `src/docs/ai-agent/demo-seed.sql`

---

## Prerequisites

1. App running: `./mvnw spring-boot:run`
2. Database: PostgreSQL with Flyway migrations applied
3. Demo seed applied (see `demo-seed.sql`)
4. `SCOPERY_SECRET_MASTER_KEY` set in `.env`
5. Real OpenAI key stored via Provider Secrets API (or use the seed which stores a placeholder)

---

## TC-01: Provider Secret — Store and Retrieve

**Goal:** Verify that a provider API key is stored encrypted and only the masked value is returned.

**Steps:**

1. `POST /api/v1/ai-agent/provider-secrets` with a fake key `sk-test-1234567890abcdef`
2. Check response: `maskedValue` should be `sk-test-***...cdef` (or similar masked form)
3. Verify `encryptedValue`, `iv`, `keyVersion` are NOT present in the response
4. Check DB: `SELECT encrypted_value, iv, key_version, masked_value FROM aiagent_provider_secret WHERE provider_id = '...'`
   - `encrypted_value` should be a Base64 string (not the raw key)
   - `masked_value` should match what the API returned

**Expected:** Secret stored encrypted; response contains only `maskedValue`.

---

## TC-02: Provider Secret — Activation and Single-Active Enforcement

**Goal:** Verify only one provider secret can be ACTIVE at a time.

**Steps:**

1. Create two provider secrets for the same provider (both with status `INACTIVE` initially)
2. `PATCH /api/v1/ai-agent/provider-secrets/{secret1Id}/activate`
3. `PATCH /api/v1/ai-agent/provider-secrets/{secret2Id}/activate`
4. Check DB: only `secret2Id` should have `status = 'ACTIVE'`
5. Call `GET /api/v1/ai-agent/provider-secrets/provider/{providerId}/active` — should return `secret2`

**Expected:** Activating a second secret deactivates the first.

---

## TC-03: Prompt Version Archiving

**Goal:** Verify that activating a prompt version archives all other ACTIVE versions of the same template.

**Steps:**

1. Create a prompt template
2. Create prompt version V1 → activate it (status becomes ACTIVE)
3. Create prompt version V2 → activate it
4. Query `GET /api/v1/ai-agent/prompt-versions?templateId={templateId}&status=ACTIVE`
   - Should return only V2
5. Query with `status=ARCHIVED` — should return V1

**Expected:** Only one ACTIVE version per template at a time.

---

## TC-04: Model Deployment Default Flag

**Goal:** Verify that `isDefault` is cleared from other deployments when a new default is set.

**Steps:**

1. Create two deployments for the same model and environment with `isDefault=false`
2. `PATCH /api/v1/ai-agent/model-deployments/{dep1Id}/set-default`
3. `PATCH /api/v1/ai-agent/model-deployments/{dep2Id}/set-default`
4. Check DB: only `dep2Id` should have `is_default = true`

**Expected:** Only one default deployment per model+environment.

---

## TC-05: End-to-End Execution (Real OpenAI Call)

**Goal:** Verify the complete execution flow from event config to AI response.

**Prerequisites:**
- Real OpenAI API key stored via Provider Secrets API and activated
- All entities from demo seed are ACTIVE
- `AIAGENT_RUNTIME_ENVIRONMENT=DEV`

**Steps:**

1. `POST /api/v1/ai-agent/executions/event-config/{eventConfigId}` with:
   ```json
   { "requestId": "tc-05-001", "inputVariables": { "document": "Spring Boot is a framework for building Java applications." } }
   ```
2. Verify response has `status: COMPLETED` and non-empty `outputText`
3. Verify `inputTokens`, `outputTokens`, `totalTokens` > 0
4. Query `GET /api/v1/ai-agent/execution-logs/{executionLogId}` — verify log is saved
5. Check `app_activity_log` table — should have an `EXECUTE_EVENT_CONFIG` entry

**Expected:** AI call succeeds; execution log saved; activity logged.

---

## TC-06: Usage Policy — BLOCK

**Goal:** Verify that a BLOCK policy with max 1 request per day prevents a second execution.

**Steps:**

1. Create and activate a usage policy:
   - `targetType=GLOBAL`, `maxRequestsPerPeriod=1`, `periodUnit=DAY`, `onViolation=BLOCK`
2. Execute once — should succeed
3. Execute again — should return HTTP 429 with `errorCode: USAGE_POLICY_EXCEEDED`
4. Check DB: no second `aiagent_execution_log` row was created for the blocked attempt

**Expected:** Second call blocked with 429; no execution log written.

---

## TC-07: Usage Policy — WARN

**Goal:** Verify that a WARN policy allows execution but returns warnings in the response.

**Steps:**

1. Create and activate a usage policy:
   - `targetType=GLOBAL`, `maxTokensPerRun=1`, `onViolation=WARN`
   (tokens per run = 1 will always trigger a warning for any real response)
2. Execute an event config
3. Verify response has `policyDecision: WARN` and non-empty `policyWarnings` list
4. Verify execution still succeeded (`status: COMPLETED`)

**Expected:** Execution completes; response includes warnings.

---

## TC-08: Playground — Event Config Run

**Goal:** Verify playground run delegates to execution service correctly.

**Steps:**

1. `POST /api/v1/ai-agent/playground/event-config/{eventConfigId}/run` with input variables
2. Verify response has `mode: EVENT_CONFIG` and `status: COMPLETED`
3. Verify an execution log was created in `aiagent_execution_log`

**Expected:** Identical to TC-05 but with `mode` field in response.

---

## TC-09: Playground — Direct Run

**Goal:** Verify playground direct run bypasses EventConfig and uses explicit IDs.

**Steps:**

1. `POST /api/v1/ai-agent/playground/direct/run` with:
   ```json
   {
     "requestId": "tc-09-001",
     "agentId": "{agentId}",
     "promptVersionId": "{promptVersionId}",
     "modelDeploymentId": "{deploymentId}",
     "inputVariables": { "document": "Test content." }
   }
   ```
2. Verify response has `mode: DIRECT` and `status: COMPLETED`
3. Check DB: execution log has `event_config_id = NULL`

**Expected:** Execution succeeds without EventConfig; log has null eventConfigId.

---

## TC-10: Playground — Prompt Preview (Partial Variables)

**Goal:** Verify prompt preview renders available variables and lists missing ones without calling the AI.

**Steps:**

1. Prompt template content: `"Summarize {{document}} in {{language}}"`
2. `POST /api/v1/ai-agent/playground/prompt/preview` with only `"document": "Hello world"`
3. Verify response:
   - `renderedPrompt` contains `"Summarize Hello world in {{language}}"`
   - `missingVariables` = `["language"]`
4. No `aiagent_execution_log` row is created

**Expected:** Partial render returned; no AI call made; missing variable identified.

---

## TC-11: Inactive Entity Guards

**Goal:** Verify that execution fails gracefully when a dependent entity is INACTIVE.

**Steps:**

1. Deactivate the ModelDeployment: `PATCH /api/v1/ai-agent/model-deployments/{id}/deactivate`
2. Call `POST /api/v1/ai-agent/executions/event-config/{eventConfigId}` that references it
3. Verify HTTP 422 response with appropriate `errorCode`

**Repeat for:** Agent, PromptVersion, PromptTemplate, Provider.

**Expected:** Each deactivated dependency produces 422 with a clear error code.

---

## TC-12: Deprecated Provider — Cannot Re-activate

**Goal:** Verify that a DEPRECATED provider cannot be activated again.

**Steps:**

1. Deactivate a provider (status → INACTIVE)
2. There is no deprecation endpoint yet — manually update DB: `UPDATE aiagent_provider SET status = 'DEPRECATED' WHERE id = '...'`
3. Try `PATCH /api/v1/ai-agent/providers/{id}/activate`
4. Expect HTTP 422 with `errorCode: DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED`

**Expected:** Activation of deprecated provider returns 422.

---

## TC-13: Execution Log — Search and Filter

**Goal:** Verify execution log search works with multiple filters.

**Steps:**

1. Run several executions with different event configs
2. `GET /api/v1/ai-agent/execution-logs?status=COMPLETED&page=0&size=5`
3. `GET /api/v1/ai-agent/execution-logs?agentId={agentId}&status=COMPLETED`
4. Verify results are filtered correctly and pagination fields (`totalElements`, `totalPages`) are accurate

**Expected:** Filtered results match DB state; pagination metadata is correct.
