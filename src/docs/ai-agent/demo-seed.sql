-- ============================================================
-- AI Agent Demo Seed Data
-- Idempotent: uses INSERT ... ON CONFLICT DO NOTHING
-- Run after all Flyway migrations have been applied.
--
-- NOTE: Provider secrets (API keys) are NOT seeded here because
-- they require AES encryption with the runtime SCOPERY_SECRET_MASTER_KEY.
-- Store provider API keys via the API after seeding:
--   POST /api/v1/ai-agent/provider-secrets
-- ============================================================

-- Fixed UUIDs for stable cross-table references
-- Replace these in your local environment if you want different ones.

-- Provider
DO $$ BEGIN
  INSERT INTO aiagent_provider (id, name, code, type, api_base_url, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'OpenAI',
    'OPENAI',
    'LLM',
    'https://api.openai.com/v1',
    'OpenAI GPT provider',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT (code) DO NOTHING;
END $$;

-- AI Model
DO $$ BEGIN
  INSERT INTO aiagent_model (id, provider_id, name, code, api_model_id, context_window, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'b0000000-0000-0000-0000-000000000001',
    'a0000000-0000-0000-0000-000000000001',
    'GPT-4o',
    'GPT_4O',
    'gpt-4o',
    128000,
    'OpenAI GPT-4o multimodal model',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT (code) DO NOTHING;
END $$;

-- Model Deployment (DEV environment, default)
DO $$ BEGIN
  INSERT INTO aiagent_model_deployment (id, model_id, name, code, environment, is_default, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'c0000000-0000-0000-0000-000000000001',
    'b0000000-0000-0000-0000-000000000001',
    'GPT-4o DEV',
    'GPT_4O_DEV',
    'DEV',
    true,
    'Default DEV deployment for GPT-4o',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT (code) DO NOTHING;
END $$;

-- Model Parameter Capabilities
DO $$ BEGIN
  INSERT INTO aiagent_model_parameter_capability (id, model_id, parameter_name, api_parameter_key, supported, min_value, max_value, if_null_behavior, description, status, created_at, updated_at, created_by, updated_by)
  VALUES
    (
      'd0000000-0000-0000-0000-000000000001',
      'b0000000-0000-0000-0000-000000000001',
      'temperature',
      'temperature',
      'YES',
      0.0,
      2.0,
      'USE_PROVIDER_DEFAULT',
      'Sampling temperature (0=deterministic, 2=creative)',
      'ACTIVE',
      NOW(), NOW(), 'SYSTEM', 'SYSTEM'
    ),
    (
      'd0000000-0000-0000-0000-000000000002',
      'b0000000-0000-0000-0000-000000000001',
      'maxOutputTokens',
      'max_output_tokens',
      'YES',
      1.0,
      16384.0,
      'USE_PROVIDER_DEFAULT',
      'Maximum number of output tokens',
      'ACTIVE',
      NOW(), NOW(), 'SYSTEM', 'SYSTEM'
    )
  ON CONFLICT DO NOTHING;
END $$;

-- Agent
DO $$ BEGIN
  INSERT INTO aiagent_agent (id, name, code, type, output_format, system_instruction, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'e0000000-0000-0000-0000-000000000001',
    'Document Summarizer',
    'DOC_SUMMARIZER',
    'TASK',
    'TEXT',
    'You are a concise and accurate document summarizer. Return a summary in the requested format.',
    'Summarizes long documents into bullet points',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT (code) DO NOTHING;
END $$;

-- Prompt Template
DO $$ BEGIN
  INSERT INTO aiagent_prompt_template (id, agent_id, name, code, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'f0000000-0000-0000-0000-000000000001',
    'e0000000-0000-0000-0000-000000000001',
    'Document Summary Template',
    'DOC_SUMMARY_TPL',
    'Template for summarizing documents using bullet points',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT DO NOTHING;
END $$;

-- Prompt Version (ACTIVE)
DO $$ BEGIN
  INSERT INTO aiagent_prompt_version (id, template_id, version_number, title, content, content_format, change_note, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'f1000000-0000-0000-0000-000000000001',
    'f0000000-0000-0000-0000-000000000001',
    1,
    'Initial version',
    'Please summarize the following document in 3 concise bullet points. Be accurate and preserve key facts.

Document:
{{document}}

Output format:
• Point 1
• Point 2
• Point 3',
    'TEXT',
    'Initial prompt version',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT DO NOTHING;
END $$;

-- Event Definition (Event Registry)
DO $$ BEGIN
  INSERT INTO app_event_definition (id, code, name, source_system, event_key, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'aa000000-0000-0000-0000-000000000001',
    'DOC_UPLOAD',
    'Document Upload',
    'SCOPERY',
    'scopery.document.uploaded',
    'Triggered when a document is uploaded to the system',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT (code) DO NOTHING;
END $$;

-- Event Config (links event → agent → prompt → deployment)
DO $$ BEGIN
  INSERT INTO aiagent_event_config (id, code, name, event_definition_id, environment, trigger_type, agent_id, prompt_version_id, model_deployment_id, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'bb000000-0000-0000-0000-000000000001',
    'DOC_UPLOAD_SUMMARY_DEV',
    'Doc Upload → Summary (DEV)',
    'aa000000-0000-0000-0000-000000000001',
    'DEV',
    'AUTOMATIC',
    'e0000000-0000-0000-0000-000000000001',
    'f1000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'Summarize documents on upload in DEV environment',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT (code) DO NOTHING;
END $$;

-- Usage Policy: GLOBAL daily request limit (WARN only — safe for testing)
DO $$ BEGIN
  INSERT INTO aiagent_usage_policy (id, code, name, target_type, target_id, max_requests_per_period, period, action, priority, description, status, created_at, updated_at, created_by, updated_by)
  VALUES (
    'cc000000-0000-0000-0000-000000000001',
    'GLOBAL_DEV_DAILY_WARN',
    'Global DEV Daily Request Warning',
    'GLOBAL',
    NULL,
    1000,
    'DAY',
    'WARN',
    100,
    'Warn when global daily requests exceed 1000 in DEV',
    'ACTIVE',
    NOW(), NOW(), 'SYSTEM', 'SYSTEM'
  )
  ON CONFLICT (code) DO NOTHING;
END $$;

-- ============================================================
-- After running this seed:
--
-- 1. Store your OpenAI API key via:
--    POST /api/v1/ai-agent/provider-secrets
--    { "providerId": "a0000000-0000-0000-0000-000000000001",
--      "secretValue": "sk-proj-...", "label": "Dev key" }
--    Then activate it:
--    PATCH /api/v1/ai-agent/provider-secrets/{id}/activate
--
-- 2. Test the full flow:
--    POST /api/v1/ai-agent/executions/event-config/bb000000-0000-0000-0000-000000000001
--    { "requestId": "seed-test-001",
--      "inputVariables": { "document": "Spring Boot makes Java web development fast and simple." } }
-- ============================================================
