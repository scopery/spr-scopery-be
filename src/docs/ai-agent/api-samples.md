# AI Agent — API Samples

Base URL: `http://localhost:8080`

All responses are wrapped in `{"success": true, "data": {...}}`.  
Paginated responses return `{"success": true, "data": {"items": [], "page": 0, "size": 20, ...}}`.

Replace UUIDs with actual values from your database.

---

## 1. Providers

### Create provider
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/providers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "OpenAI",
    "code": "OPENAI",
    "type": "LLM",
    "apiBaseUrl": "https://api.openai.com/v1",
    "description": "OpenAI GPT provider"
  }'
```

### Get provider by ID
```bash
curl http://localhost:8080/api/v1/ai-agent/providers/{id}
```

### Search providers
```bash
curl "http://localhost:8080/api/v1/ai-agent/providers?keyword=open&status=ACTIVE&page=0&size=20"
```

### Update provider
```bash
curl -X PUT http://localhost:8080/api/v1/ai-agent/providers/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "OpenAI Updated",
    "apiBaseUrl": "https://api.openai.com/v1",
    "description": "Updated description"
  }'
```

### Activate / Deactivate
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/providers/{id}/activate
curl -X PATCH http://localhost:8080/api/v1/ai-agent/providers/{id}/deactivate
```

---

## 2. Provider Secrets

### Set provider API key
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/provider-secrets \
  -H "Content-Type: application/json" \
  -d '{
    "providerId": "{providerId}",
    "secretValue": "sk-proj-...",
    "label": "Production key"
  }'
```

### Get active secret for provider (returns maskedValue only)
```bash
curl http://localhost:8080/api/v1/ai-agent/provider-secrets/provider/{providerId}/active
```

### List secrets for provider
```bash
curl "http://localhost:8080/api/v1/ai-agent/provider-secrets?providerId={providerId}"
```

### Activate / Deactivate secret
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/provider-secrets/{id}/activate
curl -X PATCH http://localhost:8080/api/v1/ai-agent/provider-secrets/{id}/deactivate
```

---

## 3. AI Models

### Create model
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/models \
  -H "Content-Type: application/json" \
  -d '{
    "providerId": "{providerId}",
    "name": "GPT-4o",
    "code": "GPT_4O",
    "apiModelId": "gpt-4o",
    "contextWindow": 128000,
    "description": "GPT-4o multimodal model"
  }'
```

### Search models
```bash
curl "http://localhost:8080/api/v1/ai-agent/models?providerId={providerId}&status=ACTIVE"
```

### Activate / Deactivate
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/models/{id}/activate
curl -X PATCH http://localhost:8080/api/v1/ai-agent/models/{id}/deactivate
```

---

## 4. Model Deployments

### Create deployment
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/model-deployments \
  -H "Content-Type: application/json" \
  -d '{
    "modelId": "{modelId}",
    "name": "GPT-4o DEV",
    "code": "GPT_4O_DEV",
    "environment": "DEV",
    "isDefault": true,
    "description": "Dev deployment for GPT-4o"
  }'
```

### Search deployments
```bash
curl "http://localhost:8080/api/v1/ai-agent/model-deployments?modelId={modelId}&environment=DEV&isDefault=true"
```

### Set as default
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/model-deployments/{id}/set-default
```

### Activate / Deactivate
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/model-deployments/{id}/activate
curl -X PATCH http://localhost:8080/api/v1/ai-agent/model-deployments/{id}/deactivate
```

---

## 5. Model Parameter Capabilities

### Create capability
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/model-parameter-capabilities \
  -H "Content-Type: application/json" \
  -d '{
    "modelId": "{modelId}",
    "parameterName": "temperature",
    "apiParameterKey": "temperature",
    "supported": "YES",
    "minValue": 0.0,
    "maxValue": 2.0,
    "ifNullBehavior": "USE_PROVIDER_DEFAULT",
    "description": "Sampling temperature"
  }'
```

### Search capabilities
```bash
curl "http://localhost:8080/api/v1/ai-agent/model-parameter-capabilities?modelId={modelId}"
```

---

## 6. Agents

### Create agent
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/agents \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Document Summarizer",
    "code": "DOC_SUMMARIZER",
    "type": "TASK",
    "outputFormat": "TEXT",
    "systemInstruction": "You are a concise document summarizer.",
    "description": "Summarizes long documents"
  }'
```

### Search agents
```bash
curl "http://localhost:8080/api/v1/ai-agent/agents?status=ACTIVE&type=TASK"
```

### Activate / Deactivate
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/agents/{id}/activate
curl -X PATCH http://localhost:8080/api/v1/ai-agent/agents/{id}/deactivate
```

---

## 7. Prompt Templates

### Create template
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/prompt-templates \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "{agentId}",
    "name": "Document Summary Template",
    "code": "DOC_SUMMARY_TPL",
    "description": "Template for summarizing documents"
  }'
```

### Search templates
```bash
curl "http://localhost:8080/api/v1/ai-agent/prompt-templates?agentId={agentId}&status=ACTIVE"
```

---

## 8. Prompt Versions

### Create version
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/prompt-versions \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": "{templateId}",
    "content": "Summarize the following document in 3 bullet points:\n\n{{document}}",
    "contentFormat": "TEXT",
    "changeNote": "Initial version"
  }'
```

### Activate version (archives other active versions)
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/prompt-versions/{id}/activate
```

### Search versions
```bash
curl "http://localhost:8080/api/v1/ai-agent/prompt-versions?templateId={templateId}&status=ACTIVE"
```

---

## 9. Event Definitions (Event Registry)

### Create event definition
```bash
curl -X POST http://localhost:8080/api/v1/event-registry/event-definitions \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Document Upload",
    "code": "DOC_UPLOAD",
    "description": "Triggered when a document is uploaded"
  }'
```

### Search event definitions
```bash
curl "http://localhost:8080/api/v1/event-registry/event-definitions?status=ACTIVE"
```

---

## 10. Event Configs

### Create event config
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/event-configs \
  -H "Content-Type: application/json" \
  -d '{
    "eventDefinitionId": "{eventDefinitionId}",
    "agentId": "{agentId}",
    "promptVersionId": "{promptVersionId}",
    "modelDeploymentId": "{deploymentId}",
    "environment": "DEV",
    "name": "Doc Upload → Summary (DEV)",
    "code": "DOC_UPLOAD_SUMMARY_DEV",
    "description": "Summarize documents on upload in DEV"
  }'
```

### Activate event config
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/event-configs/{id}/activate
```

### Search event configs
```bash
curl "http://localhost:8080/api/v1/ai-agent/event-configs?environment=DEV&status=ACTIVE"
```

---

## 11. Usage Policies

### Create usage policy
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/usage-policies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "DEV Global Limit",
    "code": "DEV_GLOBAL_LIMIT",
    "targetType": "GLOBAL",
    "targetId": null,
    "maxRequestsPerPeriod": 100,
    "periodUnit": "DAY",
    "maxTokensPerRun": 4000,
    "maxMonthlyCost": null,
    "onViolation": "BLOCK",
    "description": "Global daily request limit for DEV"
  }'
```

### Create agent-scoped WARN policy
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/usage-policies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Doc Summarizer Token Warning",
    "code": "DOC_SUMMARIZER_TOKEN_WARN",
    "targetType": "AGENT",
    "targetId": "{agentId}",
    "maxTokensPerRun": 2000,
    "onViolation": "WARN",
    "description": "Warn when token usage is high"
  }'
```

### Activate policy
```bash
curl -X PATCH http://localhost:8080/api/v1/ai-agent/usage-policies/{id}/activate
```

---

## 12. Executions

### Execute via event config
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/executions/event-config/{eventConfigId} \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": "req-001",
    "inputVariables": {
      "document": "This is the document content to summarize..."
    }
  }'
```

**Response includes:**
- `executionLogId`, `status`, `outputText`
- `inputTokens`, `outputTokens`, `totalTokens`, `estimatedCostUsd`
- `policyDecision` (ALLOWED / WARN / BLOCKED)
- `policyWarnings` (list of warning messages if any)
- `durationMs`, `completedAt`

### Search execution logs
```bash
curl "http://localhost:8080/api/v1/ai-agent/execution-logs?status=COMPLETED&agentId={agentId}&page=0&size=20"
```

### Get execution log by ID
```bash
curl http://localhost:8080/api/v1/ai-agent/execution-logs/{id}
```

---

## 13. Playground

### Run via event config (test an existing binding)
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/playground/event-config/{eventConfigId}/run \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": "playground-001",
    "inputVariables": {
      "document": "Test document content..."
    }
  }'
```

### Run direct (specify everything explicitly)
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/playground/direct/run \
  -H "Content-Type: application/json" \
  -d '{
    "requestId": "playground-direct-001",
    "agentId": "{agentId}",
    "promptVersionId": "{promptVersionId}",
    "modelDeploymentId": "{deploymentId}",
    "inputVariables": {
      "document": "Test document content..."
    }
  }'
```

### Preview prompt (renders template without calling the AI)
```bash
curl -X POST http://localhost:8080/api/v1/ai-agent/playground/prompt/preview \
  -H "Content-Type: application/json" \
  -d '{
    "promptVersionId": "{promptVersionId}",
    "inputVariables": {
      "document": "Sample document text..."
    }
  }'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "promptVersionId": "...",
    "promptTemplateId": "...",
    "renderedPrompt": "Summarize the following document in 3 bullet points:\n\nSample document text...",
    "missingVariables": [],
    "renderedAt": "2026-06-14T10:00:00Z"
  }
}
```

### Get playground options (for dropdown population)
```bash
curl "http://localhost:8080/api/v1/ai-agent/playground/options?includeEventConfigs=true&includeAgents=true&includePromptVersions=true&includeDeployments=true"
```
