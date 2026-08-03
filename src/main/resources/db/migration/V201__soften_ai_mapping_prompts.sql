-- Soften AI mapping prompts: prefer plausible SUGGEST/MEDIUM over NO_MATCH when candidates exist.
-- Also bump temperature slightly for less brittle refusals.

UPDATE aiagent_prompt_version v
SET
    system_prompt = $sys$You are the Scopery Traceability Mapping Engine.

Evaluate only the supplied source items and supplied eligible target candidates.

Rules:
1. Never invent entity IDs — select only from supplied candidate IDs.
2. Prefer a plausible mapping over NO_MATCH when at least one candidate is reasonably related
   by capability, business object, actor/action, module, or wording overlap.
3. Use NO_MATCH only when every candidate is clearly unrelated or the source is empty/nonsensical.
4. Do not require perfect full coverage. Partial coverage is enough to SUGGEST.
5. Shared domain terms (e.g. login, auth, order, payment) are valid positive signals.
6. Return concise reasonCodes and evidence. Output JSON only — no prose outside the schema.
7. Confidence bands: HIGH (strong direct match), MEDIUM (plausible / partial), LOW (weak but possible).
8. If top candidates are close, still SUGGEST the best one with MEDIUM/LOW rather than refusing.
9. Preserve exact sourceId and targetId values from the input.
10. Vietnamese and English text are both valid.$sys$,
    temperature = 0.35,
    updated_at = NOW(),
    updated_by = 'SYSTEM'
WHERE v.status = 'ACTIVE'
  AND v.template_id IN (
      SELECT id FROM aiagent_prompt_template WHERE code LIKE 'TRACE_MAP_%'
  );

UPDATE aiagent_prompt_version v
SET
    user_prompt_template = $usr$Action:
Map one or more Functions to each Requirement.

Cardinality:
Requirement N ↔ N Function.

Guidance:
- Suggest every Function that reasonably contributes coverage.
- Partial coverage is OK — do not demand one Function covers the whole Requirement.
- Prefer SUGGEST with MEDIUM/LOW over NO_MATCH when candidates share domain intent.
- Use NO_MATCH only if no candidate is related at all.

Evaluation criteria (any strong subset is enough):
1. Capability / expected outcome
2. Business action
3. Business object
4. Acceptance criteria overlap
5. Actor / role compatibility
6. Module / domain compatibility

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
}$usr$,
    updated_at = NOW()
WHERE v.status = 'ACTIVE'
  AND v.template_id = (SELECT id FROM aiagent_prompt_template WHERE code = 'TRACE_MAP_REQ_FUNC_V1');

UPDATE aiagent_prompt_version v
SET
    user_prompt_template = $usr$Action:
Map each Use Case to exactly one parent Function.

Cardinality:
Function 1 → N Use Cases. Each Use Case has at most one parent Function.

Guidance:
- Pick the single best supplied Function candidate.
- Prefer SUGGEST with MEDIUM/LOW over NO_MATCH when a candidate is a reasonable parent.
- Use NO_MATCH only if no candidate fits at all.

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
          "reasonCodes": ["string"],
          "evidence": ["string"],
          "warnings": ["string"]
        }
      ]
    }
  ]
}$usr$,
    updated_at = NOW()
WHERE v.status = 'ACTIVE'
  AND v.template_id = (SELECT id FROM aiagent_prompt_template WHERE code = 'TRACE_MAP_UC_FUNCTION_V1');

UPDATE aiagent_prompt_version v
SET
    user_prompt_template = $usr$Action:
Map each functional Test Case to exactly one parent Use Case.

Cardinality:
Use Case 1 → N functional Test Cases. Each Test Case has at most one parent Use Case.

Guidance:
- Pick the single best supplied Use Case candidate.
- Prefer SUGGEST with MEDIUM/LOW over NO_MATCH when a candidate is a reasonable parent.
- Use NO_MATCH only if no candidate fits at all.

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
          "reasonCodes": ["string"],
          "evidence": ["string"],
          "warnings": ["string"]
        }
      ]
    }
  ]
}$usr$,
    updated_at = NOW()
WHERE v.status = 'ACTIVE'
  AND v.template_id = (SELECT id FROM aiagent_prompt_template WHERE code = 'TRACE_MAP_TC_UC_V1');
