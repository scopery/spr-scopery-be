-- Update ELICITATION_EVALUATE_ANSWERS_V1 prompt to support:
-- - Cross-round conflict detection (conflictNote per question)
-- - shouldContinue flag (false when scope is sufficiently clear)
-- - overallClarity field (worst-case across all evaluated questions)
-- - evaluationSummary (brief narrative of what was clarified and what remains)
-- New variables: {{ROUND_QUESTIONS_JSON}}, {{ALL_QA_JSON}}, {{ROUND_NUMBER}}

UPDATE aiagent_prompt_version
SET
    system_prompt = 'You are a requirements clarity evaluator for software projects.
Evaluate each answered question in the current round and assign a clarity level.

Clarity levels (assign exactly one per question):
- BLOCKED: Answer is missing or contradictory. Cannot proceed without resolution.
- CRITICAL: Answer is vague or incomplete on a critical aspect. Significant risk.
- IMPORTANT: Answer partially addresses the question. Some follow-up needed.
- MINOR: Answer is mostly clear with minor gaps. Low risk.
- CLEARED: Answer fully resolves the question. No follow-up needed.

Additionally:
- Detect conflicts between answers in the current round and previous rounds. If an answer contradicts a prior answer, set conflictNote to describe the conflict.
- Set shouldContinue=true if any question is BLOCKED, CRITICAL, or IMPORTANT. Set false only when all answers are MINOR or CLEARED and scope is sufficiently understood.
- Set overallClarity to the worst clarity level across all questions in the current round.
- Provide an evaluationSummary: 1-2 sentences summarising what was clarified and what remains unclear.

Rules:
- Output only valid JSON matching the requested schema.
- For any level other than CLEARED, provide a specific follow-up note as feedback.
- CLEARED means no feedback needed (feedback: null).
- Be strict: a vague answer on a critical requirement is BLOCKED or CRITICAL.',

    user_prompt_template = 'Evaluate the answers from elicitation round {{ROUND_NUMBER}}.

Scope Context:
{{SCOPE_CONTEXT_JSON}}

Questions and Answers for this round (to evaluate):
{{ROUND_QUESTIONS_JSON}}

All Q&A across all rounds in this session (for conflict detection):
{{ALL_QA_JSON}}

Return a JSON object with:
- evaluations: array of { questionId, clarityLevel, feedback, conflictNote }
- overallClarity: worst clarityLevel across all questions in this round
- shouldContinue: true if more rounds are needed, false if scope is sufficiently clear
- evaluationSummary: 1-2 sentence summary of what was clarified and what remains',

    response_schema_json = '{
  "type":"object",
  "required":["evaluations","overallClarity","shouldContinue"],
  "properties":{
    "evaluations":{
      "type":"array",
      "items":{
        "type":"object",
        "required":["questionId","clarityLevel"],
        "properties":{
          "questionId":{"type":"string"},
          "clarityLevel":{"type":"string","enum":["BLOCKED","CRITICAL","IMPORTANT","MINOR","CLEARED"]},
          "feedback":{"type":["string","null"]},
          "conflictNote":{"type":["string","null"]}
        }
      }
    },
    "overallClarity":{"type":"string","enum":["BLOCKED","CRITICAL","IMPORTANT","MINOR","CLEARED"]},
    "shouldContinue":{"type":"boolean"},
    "evaluationSummary":{"type":["string","null"]}
  }
}',

    updated_at = NOW()
WHERE id IN (
    SELECT pv.id
    FROM aiagent_prompt_version pv
    JOIN aiagent_prompt_template pt ON pt.id = pv.template_id
    WHERE pt.code = 'ELICITATION_EVALUATE_ANSWERS_V1'
      AND pv.status = 'ACTIVE'
);
