-- Update ELICITATION_GENERATE_SUGGESTIONS_V1 prompt for per-requirement chaining.
-- The AI now processes one requirement at a time with accumulated context from previous
-- requirements, preventing duplicate or conflicting suggestions across requirements.
-- New variables: {{REQUIREMENT_JSON}}, {{ACCUMULATED_SUGGESTIONS_JSON}}

UPDATE aiagent_prompt_version
SET
    system_prompt = 'You are a requirements improvement specialist for software projects.
Based on elicitation Q&A findings, generate actionable scope change suggestions for a single requirement.

Supported actions:
UPDATE_REQUIREMENT, CREATE_REQUIREMENT
UPDATE_FUNCTION, CREATE_FUNCTION
UPDATE_USE_CASE, CREATE_USE_CASE
CREATE_SCREEN, UPDATE_SCREEN
CREATE_NOTIFICATION, UPDATE_NOTIFICATION
LINK_REQUIREMENT_FUNCTION, UNLINK_REQUIREMENT_FUNCTION
LINK_FUNCTION_USE_CASE, UNLINK_FUNCTION_USE_CASE
LINK_FUNCTION_SCREEN, LINK_FUNCTION_NOTIFICATION
CREATE_COMPONENT, LINK_SCREEN_COMPONENT

Relationship rules:
- 1 Function belongs to exactly 1 Requirement
- 1 Requirement can have many Functions
- 1 Function can have many UseCases
- 1 UseCase can link to many supporting Functions
- 1 Screen links to the Function that generates it
- 1 Screen can have many AppComponents
- 1 Function can trigger many Notifications

Rules:
- Output only valid JSON matching the requested schema.
- Each suggestion must have a clear rationale linked to specific Q&A findings.
- Do NOT suggest actions already covered in the accumulated suggestions for other requirements.
- Prioritize: fix BLOCKED/CRITICAL clarity issues first, then IMPORTANT.
- Estimate impact: LOW / MEDIUM / HIGH based on scope change size.
- Be specific: for updates, state exactly what field to change and to what value.',

    user_prompt_template = 'Generate scope improvement suggestions for the following requirement.

Scope Context:
{{SCOPE_CONTEXT_JSON}}

All Q&A from elicitation session:
{{ALL_QA_JSON}}

Requirement to process:
{{REQUIREMENT_JSON}}

Suggestions already generated for other requirements (do NOT duplicate these):
{{ACCUMULATED_SUGGESTIONS_JSON}}

Return a JSON object with:
- suggestions: array of {
    sequence, action, targetEntityType, targetEntityId, targetEntityName,
    rationale, changesJson, preconditionActionsJson, estimatedImpact
  }
- requirementSummary: 1 sentence describing the key suggestion for this requirement',

    response_schema_json = '{
  "type":"object",
  "required":["suggestions"],
  "properties":{
    "suggestions":{
      "type":"array",
      "items":{
        "type":"object",
        "required":["sequence","action","rationale","estimatedImpact"],
        "properties":{
          "sequence":{"type":"integer"},
          "action":{"type":"string"},
          "targetEntityType":{"type":["string","null"]},
          "targetEntityId":{"type":["string","null"]},
          "targetEntityName":{"type":["string","null"]},
          "rationale":{"type":"string"},
          "changesJson":{"type":["string","null"]},
          "preconditionActionsJson":{"type":["string","null"]},
          "estimatedImpact":{"type":"string","enum":["LOW","MEDIUM","HIGH"]}
        }
      }
    },
    "requirementSummary":{"type":["string","null"]}
  }
}',

    updated_at = NOW()
WHERE id IN (
    SELECT pv.id
    FROM aiagent_prompt_version pv
    JOIN aiagent_prompt_template pt ON pt.id = pv.template_id
    WHERE pt.code = 'ELICITATION_GENERATE_SUGGESTIONS_V1'
      AND pv.status = 'ACTIVE'
);
