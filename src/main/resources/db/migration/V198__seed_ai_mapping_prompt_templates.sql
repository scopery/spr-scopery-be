-- Seed a system agent for AI-assisted traceability mapping prompts.
-- This agent owns the 3 seed prompt templates and is not tied to any model deployment.
INSERT INTO aiagent_agent (id, name, code, type, description, default_model_deployment_id,
                           output_format, status, created_at, updated_at, created_by, updated_by)
VALUES (gen_random_uuid(),
        'Traceability Mapping System',
        'TRACE_MAPPING_SYSTEM',
        'SYSTEM',
        'System agent that owns seed prompts for AI-assisted traceability auto-mapping.',
        NULL,
        'JSON',
        'ACTIVE',
        NOW(), NOW(), 'SYSTEM', 'SYSTEM')
ON CONFLICT (code) DO NOTHING;

-- Capture the system agent id for use in prompt template inserts
DO
$$
    DECLARE
        v_agent_id UUID;
        v_tpl_req_fn UUID := gen_random_uuid();
        v_tpl_uc_fn  UUID := gen_random_uuid();
        v_tpl_tc_uc  UUID := gen_random_uuid();
    BEGIN
        SELECT id INTO v_agent_id FROM aiagent_agent WHERE code = 'TRACE_MAPPING_SYSTEM';

        -- Prompt Template: Map Functions to Requirements
        INSERT INTO aiagent_prompt_template (id, agent_id, name, code, description, status,
                                            created_at, updated_at, created_by, updated_by)
        VALUES (v_tpl_req_fn, v_agent_id,
                'Traceability: Map Functions to Requirements',
                'TRACE_MAP_REQ_FUNC_V1',
                'Evaluates candidate Functions for each Requirement and suggests N:N mappings.',
                'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM')
        ON CONFLICT DO NOTHING;

        -- Prompt Version V1 for TRACE_MAP_REQ_FUNC_V1
        INSERT INTO aiagent_prompt_version (id, template_id, version_number, title, content,
                                           system_prompt, user_prompt_template,
                                           content_format, response_format, response_schema_json,
                                           temperature, max_tokens,
                                           status, activated_at, activated_by,
                                           created_at, updated_at, created_by, updated_by)
        VALUES (gen_random_uuid(), v_tpl_req_fn, 1,
                'Seed v1 — Requirement to Function mapping',
                'See system_prompt and user_prompt_template.',
                -- system_prompt: shared rules from spec §10
                'You are the Scopery Traceability Mapping Engine.

Your job is to evaluate only the supplied source items and supplied eligible target candidates.

You must follow these rules:

1. Never invent entities, IDs, fields, relationships, actors, flows, requirements, functions, use cases, or test cases.
2. Select only from the supplied candidate IDs.
3. Respect the relation cardinality and action-specific rules.
4. Return NO_MATCH when no candidate is sufficiently suitable.
5. Do not force a mapping because candidates exist.
6. Prefer direct semantic and behavioral coverage over keyword similarity.
7. Consider actor, action, business object, constraints, outcome, acceptance criteria, flow behavior, and functional scope.
8. Treat Module similarity as a useful signal, not universal proof.
9. A shared generic word is not sufficient evidence for a mapping.
10. Do not modify or remove existing confirmed mappings.
11. Existing mappings may be used only as context when provided.
12. Return concise reason codes and short evidence statements.
13. Do not output prose outside the required JSON schema.
14. Use only these confidence bands: HIGH, MEDIUM, LOW.
15. If the top two candidates are close, return AMBIGUOUS.
16. If source information is insufficient, return NO_MATCH or LOW confidence.
17. Archived or ineligible candidates will not be provided. Do not infer missing candidates.
18. Preserve the exact source and target IDs supplied by the application.',
                -- user_prompt_template: action-specific from spec §11
                'Action:
Map one or more Functions to each Requirement.

Cardinality:
Requirement N ↔ N Function.

For each Requirement:
- evaluate each supplied Function candidate independently;
- suggest every Function that directly contributes meaningful coverage;
- do not suggest duplicate existing mappings;
- identify the Requirement parts covered by each Function;
- identify important Requirement parts that remain uncovered;
- return NO_MATCH when no candidate provides meaningful coverage.

Evaluation criteria:
1. Capability and expected outcome match.
2. Business action match.
3. Business object match.
4. Acceptance Criteria coverage.
5. Actor and role compatibility.
6. Business rule and constraint match.
7. Module/domain compatibility.

Do not map a Function only because it shares generic terms.
Do not assume one Function must cover the whole Requirement.
A Requirement may require multiple Functions.

Input JSON:
{{INPUT_JSON}}

Return JSON matching this schema exactly:
{
  "results": [
    {
      "sourceId": "string",
      "decision": "SUGGEST | NO_MATCH",
      "suggestions": [
        {
          "targetId": "string",
          "rank": 1,
          "score": 0.0,
          "confidenceBand": "HIGH | MEDIUM | LOW",
          "coverageContribution": ["string"],
          "reasonCodes": ["string"],
          "evidence": ["string"],
          "warnings": ["string"]
        }
      ],
      "uncoveredRequirementParts": ["string"]
    }
  ]
}',
                'TEXT', 'JSON', NULL, 0.2, 4096,
                'ACTIVE', NOW(), 'SYSTEM',
                NOW(), NOW(), 'SYSTEM', 'SYSTEM')
        ON CONFLICT DO NOTHING;

        -- Prompt Template: Map Use Cases to Functions
        INSERT INTO aiagent_prompt_template (id, agent_id, name, code, description, status,
                                            created_at, updated_at, created_by, updated_by)
        VALUES (v_tpl_uc_fn, v_agent_id,
                'Traceability: Map Use Cases to Functions',
                'TRACE_MAP_UC_FUNCTION_V1',
                'Maps each Use Case to exactly one parent Function (1:N cardinality).',
                'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM')
        ON CONFLICT DO NOTHING;

        INSERT INTO aiagent_prompt_version (id, template_id, version_number, title, content,
                                           system_prompt, user_prompt_template,
                                           content_format, response_format, response_schema_json,
                                           temperature, max_tokens,
                                           status, activated_at, activated_by,
                                           created_at, updated_at, created_by, updated_by)
        VALUES (gen_random_uuid(), v_tpl_uc_fn, 1,
                'Seed v1 — Use Case to Function mapping',
                'See system_prompt and user_prompt_template.',
                'You are the Scopery Traceability Mapping Engine.

Your job is to evaluate only the supplied source items and supplied eligible target candidates.

You must follow these rules:

1. Never invent entities, IDs, fields, relationships, actors, flows, requirements, functions, use cases, or test cases.
2. Select only from the supplied candidate IDs.
3. Respect the relation cardinality and action-specific rules.
4. Return NO_MATCH when no candidate is sufficiently suitable.
5. Do not force a mapping because candidates exist.
6. Prefer direct semantic and behavioral coverage over keyword similarity.
7. Consider actor, action, business object, constraints, outcome, acceptance criteria, flow behavior, and functional scope.
8. Treat Module similarity as a useful signal, not universal proof.
9. A shared generic word is not sufficient evidence for a mapping.
10. Do not modify or remove existing confirmed mappings.
11. Existing mappings may be used only as context when provided.
12. Return concise reason codes and short evidence statements.
13. Do not output prose outside the required JSON schema.
14. Use only these confidence bands: HIGH, MEDIUM, LOW.
15. If the top two candidates are close, return AMBIGUOUS.
16. If source information is insufficient, return NO_MATCH or LOW confidence.
17. Archived or ineligible candidates will not be provided. Do not infer missing candidates.
18. Preserve the exact source and target IDs supplied by the application.',
                'Action:
Map each Use Case to exactly one parent Function.

Cardinality:
Function 1 → N Use Cases.
Each Use Case has at most one parent Function.

For each Use Case:
- select the single best supplied Function candidate;
- compare the Use Case goal with the Function purpose;
- compare actor, trigger, preconditions, business action, business object, success outcome, and referenced functional objects;
- consider linked Screens, APIs, and Communication Specifications;
- return AMBIGUOUS when the two best candidates are too similar;
- return NO_MATCH when no candidate is suitable.

Evaluation criteria:
1. Use Case goal vs Function purpose.
2. Success outcome match.
3. Primary actor compatibility.
4. Trigger and precondition compatibility.
5. Business action and object match.
6. Flow reference scope overlap.
7. Module/domain compatibility.

Do not select more than one Function.
Do not select a broad generic Function when a more direct Function exists.
Do not replace an existing confirmed parent unless the input explicitly requests review of an existing mapping.

Input JSON:
{{INPUT_JSON}}

Return JSON matching this schema exactly:
{
  "results": [
    {
      "sourceId": "string",
      "decision": "SUGGEST | AMBIGUOUS | NO_MATCH",
      "targetId": "string | null",
      "rank": 1,
      "score": 0.0,
      "secondBestScore": 0.0,
      "scoreMargin": 0.0,
      "confidenceBand": "HIGH | MEDIUM | LOW",
      "reasonCodes": ["string"],
      "evidence": ["string"],
      "warnings": ["string"]
    }
  ]
}',
                'TEXT', 'JSON', NULL, 0.2, 4096,
                'ACTIVE', NOW(), 'SYSTEM',
                NOW(), NOW(), 'SYSTEM', 'SYSTEM')
        ON CONFLICT DO NOTHING;

        -- Prompt Template: Map Test Cases to Use Cases
        INSERT INTO aiagent_prompt_template (id, agent_id, name, code, description, status,
                                            created_at, updated_at, created_by, updated_by)
        VALUES (v_tpl_tc_uc, v_agent_id,
                'Traceability: Map Test Cases to Use Cases',
                'TRACE_MAP_TC_UC_V1',
                'Maps each functional Test Case to exactly one parent Use Case (1:N cardinality).',
                'ACTIVE', NOW(), NOW(), 'SYSTEM', 'SYSTEM')
        ON CONFLICT DO NOTHING;

        INSERT INTO aiagent_prompt_version (id, template_id, version_number, title, content,
                                           system_prompt, user_prompt_template,
                                           content_format, response_format, response_schema_json,
                                           temperature, max_tokens,
                                           status, activated_at, activated_by,
                                           created_at, updated_at, created_by, updated_by)
        VALUES (gen_random_uuid(), v_tpl_tc_uc, 1,
                'Seed v1 — Test Case to Use Case mapping',
                'See system_prompt and user_prompt_template.',
                'You are the Scopery Traceability Mapping Engine.

Your job is to evaluate only the supplied source items and supplied eligible target candidates.

You must follow these rules:

1. Never invent entities, IDs, fields, relationships, actors, flows, requirements, functions, use cases, or test cases.
2. Select only from the supplied candidate IDs.
3. Respect the relation cardinality and action-specific rules.
4. Return NO_MATCH when no candidate is sufficiently suitable.
5. Do not force a mapping because candidates exist.
6. Prefer direct semantic and behavioral coverage over keyword similarity.
7. Consider actor, action, business object, constraints, outcome, acceptance criteria, flow behavior, and functional scope.
8. Treat Module similarity as a useful signal, not universal proof.
9. A shared generic word is not sufficient evidence for a mapping.
10. Do not modify or remove existing confirmed mappings.
11. Existing mappings may be used only as context when provided.
12. Return concise reason codes and short evidence statements.
13. Do not output prose outside the required JSON schema.
14. Use only these confidence bands: HIGH, MEDIUM, LOW.
15. If the top two candidates are close, return AMBIGUOUS.
16. If source information is insufficient, return NO_MATCH or LOW confidence.
17. Archived or ineligible candidates will not be provided. Do not infer missing candidates.
18. Preserve the exact source and target IDs supplied by the application.',
                'Action:
Map each functional Test Case to exactly one parent Use Case.

Cardinality:
Use Case 1 → N functional Test Cases.
Each functional Test Case has at most one parent Use Case.

For each Test Case:
- select the single best supplied Use Case candidate;
- compare the Test scenario with Main, Alternative, and Exception Flows;
- compare preconditions, test data, actions, expected result, actor, business object, and Acceptance Criteria;
- identify whether the Test Case primarily validates a Main Flow, Alternative Flow, Exception Flow, or general Acceptance Criterion;
- return AMBIGUOUS when top candidates are too close;
- return NO_MATCH when no candidate is suitable.

Evaluation criteria:
1. Test scenario vs Use Case flow.
2. Expected result vs Use Case outcome.
3. Preconditions and test data compatibility.
4. Acceptance Criteria coverage.
5. Actor and business object match.
6. Module and Function context.

Exclude NFR verification cases.
Do not select more than one Use Case.
Do not map based only on similar names.

Input JSON:
{{INPUT_JSON}}

Return JSON matching this schema exactly:
{
  "results": [
    {
      "sourceId": "string",
      "decision": "SUGGEST | AMBIGUOUS | NO_MATCH",
      "targetId": "string | null",
      "rank": 1,
      "score": 0.0,
      "secondBestScore": 0.0,
      "scoreMargin": 0.0,
      "confidenceBand": "HIGH | MEDIUM | LOW",
      "coveredScenarioType": "MAIN_FLOW | ALTERNATIVE_FLOW | EXCEPTION_FLOW | ACCEPTANCE_CRITERION | UNKNOWN",
      "reasonCodes": ["string"],
      "evidence": ["string"],
      "warnings": ["string"]
    }
  ]
}',
                'TEXT', 'JSON', NULL, 0.2, 4096,
                'ACTIVE', NOW(), 'SYSTEM',
                NOW(), NOW(), 'SYSTEM', 'SYSTEM')
        ON CONFLICT DO NOTHING;
    END
$$;
