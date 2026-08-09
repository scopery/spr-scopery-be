-- Update ELICITATION_GENERATE_QUESTIONS_V1 prompt to include suggestedAnswers per question.
-- The AI now returns 3-4 suggested answer options alongside each question so the FE can
-- present radio choices without a separate AI call per "Answer" click.

UPDATE aiagent_prompt_version
SET
    system_prompt = 'You are a requirements elicitation specialist for software projects.
Analyze the provided scope and generate targeted clarification questions to surface ambiguities.
Rules:
- Output only valid JSON matching the requested schema.
- Generate 5-15 questions ordered by importance (most critical first).
- Cover: unclear requirements, missing acceptance criteria, ambiguous business rules, technical dependencies, edge cases.
- Each question must be specific and answerable. No vague questions.
- Assign a category to each question: REQUIREMENT, FUNCTION, SCREEN, API, ENTITY, COMPONENT, BUSINESS_RULE
- For each question, provide 3-4 concise suggested answers that cover the most likely valid responses.
  Suggested answers should be short (1-2 sentences max) and mutually exclusive where possible.',

    user_prompt_template = 'Generate clarification questions for the following scope to reduce ambiguity before specification writing.

Scope Name: {{SCOPE_NAME}}

Requirements:
{{REQUIREMENTS_JSON}}

Functions:
{{FUNCTIONS_JSON}}

Use Cases:
{{USE_CASES_JSON}}

Screens:
{{SCREENS_JSON}}

APIs:
{{APIS_JSON}}

Data Entities:
{{ENTITIES_JSON}}

Components:
{{COMPONENTS_JSON}}

Return a JSON object with:
- questions: array of { sequence, questionText, category, suggestedAnswers }
  where suggestedAnswers is an array of 3-4 short answer options for that question.',

    response_schema_json = '{
  "type":"object",
  "required":["questions"],
  "properties":{
    "questions":{
      "type":"array",
      "items":{
        "type":"object",
        "required":["sequence","questionText","category","suggestedAnswers"],
        "properties":{
          "sequence":{"type":"integer"},
          "questionText":{"type":"string"},
          "category":{"type":"string","enum":["REQUIREMENT","FUNCTION","SCREEN","API","ENTITY","COMPONENT","BUSINESS_RULE"]},
          "suggestedAnswers":{"type":"array","items":{"type":"string"},"minItems":2,"maxItems":5}
        }
      }
    }
  }
}',

    updated_at = NOW()
WHERE id IN (
    SELECT pv.id
    FROM aiagent_prompt_version pv
    JOIN aiagent_prompt_template pt ON pt.id = pv.template_id
    WHERE pt.code = 'ELICITATION_GENERATE_QUESTIONS_V1'
      AND pv.status = 'ACTIVE'
);
