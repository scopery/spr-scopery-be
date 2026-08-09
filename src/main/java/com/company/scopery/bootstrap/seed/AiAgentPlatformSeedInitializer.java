package com.company.scopery.bootstrap.seed;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelType;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.valueobject.AiModelCode;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.valueobject.ModelDeploymentCode;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.domain.valueobject.PromptTemplateCode;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Order(20)
public class AiAgentPlatformSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiAgentPlatformSeedInitializer.class);

    private final ProviderRepository providerRepository;
    private final AiModelRepository aiModelRepository;
    private final ModelDeploymentRepository deploymentRepository;
    private final AgentRepository agentRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final PromptVersionRepository promptVersionRepository;

    public AiAgentPlatformSeedInitializer(ProviderRepository providerRepository,
                                          AiModelRepository aiModelRepository,
                                          ModelDeploymentRepository deploymentRepository,
                                          AgentRepository agentRepository,
                                          PromptTemplateRepository promptTemplateRepository,
                                          PromptVersionRepository promptVersionRepository) {
        this.providerRepository = providerRepository;
        this.aiModelRepository = aiModelRepository;
        this.deploymentRepository = deploymentRepository;
        this.agentRepository = agentRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.promptVersionRepository = promptVersionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            Map<String, UUID> providerIds = seedProviders();
            Map<String, UUID> modelIds = seedModels(providerIds);
            Map<String, UUID> deploymentIds = seedDeployments(modelIds);
            Map<String, UUID> agentIds = seedAgents(deploymentIds);
            seedPromptTemplatesAndVersions(agentIds);
            log.info("[AiAgentPlatformSeed] Seeding complete — {} providers, {} models, {} deployments, {} agents",
                    providerIds.size(), modelIds.size(), deploymentIds.size(), agentIds.size());
        } catch (Exception e) {
            log.error("[AiAgentPlatformSeed] Seeding failed — app will continue but AI config may be incomplete", e);
        }
    }

    // ── Providers ─────────────────────────────────────────────────────────────

    private Map<String, UUID> seedProviders() {
        Map<String, UUID> ids = new HashMap<>();
        providerRepository.findAll(null, null, null, PageQuery.of(0, 100))
                .content().forEach(p -> ids.put(p.code().value(), p.id()));

        ids.computeIfAbsent("OPENAI", k -> providerRepository.save(
                Provider.create("OpenAI", ProviderCode.of("OPENAI"), ProviderType.LLM,
                        "https://api.openai.com/v1", "OpenAI GPT models")).id());

        ids.computeIfAbsent("ANTHROPIC", k -> providerRepository.save(
                Provider.create("Anthropic", ProviderCode.of("ANTHROPIC"), ProviderType.LLM,
                        "https://api.anthropic.com/v1", "Anthropic Claude models")).id());

        return ids;
    }

    // ── AI Models ─────────────────────────────────────────────────────────────

    private Map<String, UUID> seedModels(Map<String, UUID> providerIds) {
        Map<String, UUID> ids = new HashMap<>();
        aiModelRepository.findAll(null, null, null, null, PageQuery.of(0, 100))
                .content().forEach(m -> ids.put(m.code().value(), m.id()));

        UUID openaiId = providerIds.get("OPENAI");
        UUID anthropicId = providerIds.get("ANTHROPIC");

        ids.computeIfAbsent("OPENAI_GPT4O", k -> aiModelRepository.save(
                AiModel.create(openaiId, "GPT-4o", AiModelCode.of("OPENAI_GPT4O"),
                        "gpt-4o", AiModelType.CHAT, "OpenAI GPT-4o multimodal model",
                        true, false, true, true, 128000, 16384, "GPT-4", null)).id());

        ids.computeIfAbsent("OPENAI_GPT4O_MINI", k -> aiModelRepository.save(
                AiModel.create(openaiId, "GPT-4o mini", AiModelCode.of("OPENAI_GPT4O_MINI"),
                        "gpt-4o-mini", AiModelType.CHAT, "OpenAI GPT-4o mini — fast and cost-effective",
                        true, false, true, true, 128000, 16384, "GPT-4", null)).id());

        ids.computeIfAbsent("ANTHROPIC_CLAUDE_OPUS_4", k -> aiModelRepository.save(
                AiModel.create(anthropicId, "Claude Opus 4", AiModelCode.of("ANTHROPIC_CLAUDE_OPUS_4"),
                        "claude-opus-4-5", AiModelType.CHAT, "Anthropic Claude Opus 4 — deep reasoning",
                        true, false, false, true, 200000, 32000, "Claude-4", null)).id());

        ids.computeIfAbsent("ANTHROPIC_CLAUDE_SONNET_4", k -> aiModelRepository.save(
                AiModel.create(anthropicId, "Claude Sonnet 4", AiModelCode.of("ANTHROPIC_CLAUDE_SONNET_4"),
                        "claude-sonnet-4-6", AiModelType.CHAT, "Anthropic Claude Sonnet 4 — balanced performance",
                        true, false, false, true, 200000, 16000, "Claude-4", null)).id());

        return ids;
    }

    // ── Model Deployments ──────────────────────────────────────────────────────

    private Map<String, UUID> seedDeployments(Map<String, UUID> modelIds) {
        Map<String, UUID> ids = new HashMap<>();
        deploymentRepository.findAll(null, null, null, null, null, PageQuery.of(0, 100))
                .content().forEach(d -> ids.put(d.code().value(), d.id()));

        ids.computeIfAbsent("OPENAI_GPT4O_PRIMARY", k -> deploymentRepository.save(
                ModelDeployment.create(modelIds.get("OPENAI_GPT4O"), "GPT-4o Primary",
                        ModelDeploymentCode.of("OPENAI_GPT4O_PRIMARY"),
                        ModelDeploymentEnvironment.PROD, "OPENAI_GPT4O_PRIMARY", null,
                        new BigDecimal("0.3"), 4096, true, "Primary GPT-4o deployment")).id());

        ids.computeIfAbsent("OPENAI_GPT4O_MINI_PRIMARY", k -> deploymentRepository.save(
                ModelDeployment.create(modelIds.get("OPENAI_GPT4O_MINI"), "GPT-4o mini Primary",
                        ModelDeploymentCode.of("OPENAI_GPT4O_MINI_PRIMARY"),
                        ModelDeploymentEnvironment.PROD, "OPENAI_GPT4O_MINI_PRIMARY", null,
                        new BigDecimal("0.3"), 2048, false, "Primary GPT-4o mini deployment")).id());

        ids.computeIfAbsent("ANTHROPIC_OPUS4_PRIMARY", k -> deploymentRepository.save(
                ModelDeployment.create(modelIds.get("ANTHROPIC_CLAUDE_OPUS_4"), "Claude Opus 4 Primary",
                        ModelDeploymentCode.of("ANTHROPIC_OPUS4_PRIMARY"),
                        ModelDeploymentEnvironment.PROD, "ANTHROPIC_OPUS4_PRIMARY", null,
                        new BigDecimal("0.2"), 8192, false, "Primary Claude Opus 4 deployment")).id());

        ids.computeIfAbsent("ANTHROPIC_SONNET4_PRIMARY", k -> deploymentRepository.save(
                ModelDeployment.create(modelIds.get("ANTHROPIC_CLAUDE_SONNET_4"), "Claude Sonnet 4 Primary",
                        ModelDeploymentCode.of("ANTHROPIC_SONNET4_PRIMARY"),
                        ModelDeploymentEnvironment.PROD, "ANTHROPIC_SONNET4_PRIMARY", null,
                        new BigDecimal("0.3"), 4096, false, "Primary Claude Sonnet 4 deployment")).id());

        return ids;
    }

    // ── Agents ────────────────────────────────────────────────────────────────

    private Map<String, UUID> seedAgents(Map<String, UUID> deploymentIds) {
        Map<String, UUID> ids = new HashMap<>();
        agentRepository.findAll(null, null, null, null, PageQuery.of(0, 100))
                .content().forEach(a -> ids.put(a.code().value(), a.id()));

        UUID gpt4oPrimary = deploymentIds.get("OPENAI_GPT4O_PRIMARY");
        UUID gpt4oMiniPrimary = deploymentIds.get("OPENAI_GPT4O_MINI_PRIMARY");
        UUID opus4Primary = deploymentIds.get("ANTHROPIC_OPUS4_PRIMARY");

        ids.computeIfAbsent("TRACE_MAPPING_SYSTEM", k -> agentRepository.save(
                Agent.create("Trace Mapping Agent", AgentCode.of("TRACE_MAPPING_SYSTEM"),
                        AgentType.SYSTEM, "Maps requirements to functions and use cases via AI",
                        gpt4oPrimary, AgentOutputFormat.JSON)).id());

        ids.computeIfAbsent("PROJECT_BRIEF_AGENT", k -> agentRepository.save(
                Agent.create("Project Brief Generator", AgentCode.of("PROJECT_BRIEF_AGENT"),
                        AgentType.SYSTEM, "Generates structured project briefs from scope snapshots",
                        gpt4oPrimary, AgentOutputFormat.JSON)).id());

        ids.computeIfAbsent("KNOWLEDGE_QUERY_REWRITER_AGENT", k -> agentRepository.save(
                Agent.create("Query Rewriter", AgentCode.of("KNOWLEDGE_QUERY_REWRITER_AGENT"),
                        AgentType.SYSTEM, "Rewrites user queries into optimized semantic search keywords",
                        gpt4oMiniPrimary, AgentOutputFormat.TEXT)).id());

        ids.computeIfAbsent("SPEC_PACK_OUTLINE_AGENT", k -> agentRepository.save(
                Agent.create("Spec Pack Outline Generator", AgentCode.of("SPEC_PACK_OUTLINE_AGENT"),
                        AgentType.SYSTEM, "Generates specification document outline from scope entities",
                        gpt4oPrimary, AgentOutputFormat.JSON)).id());

        ids.computeIfAbsent("SPEC_PACK_SECTION_AGENT", k -> agentRepository.save(
                Agent.create("Spec Pack Section Generator", AgentCode.of("SPEC_PACK_SECTION_AGENT"),
                        AgentType.SYSTEM, "Generates content blocks for a single specification section",
                        gpt4oPrimary, AgentOutputFormat.JSON)).id());

        ids.computeIfAbsent("ELICITATION_QUESTION_AGENT", k -> agentRepository.save(
                Agent.create("Elicitation Question Generator", AgentCode.of("ELICITATION_QUESTION_AGENT"),
                        AgentType.SYSTEM, "Generates clarification questions from scope to reduce ambiguity",
                        opus4Primary, AgentOutputFormat.JSON)).id());

        ids.computeIfAbsent("ELICITATION_EVALUATOR_AGENT", k -> agentRepository.save(
                Agent.create("Elicitation Answer Evaluator", AgentCode.of("ELICITATION_EVALUATOR_AGENT"),
                        AgentType.SYSTEM, "Evaluates elicitation answers and assigns 5-level clarity scores",
                        opus4Primary, AgentOutputFormat.JSON)).id());

        ids.computeIfAbsent("ELICITATION_SUGGESTION_AGENT", k -> agentRepository.save(
                Agent.create("Elicitation Suggestion Generator", AgentCode.of("ELICITATION_SUGGESTION_AGENT"),
                        AgentType.SYSTEM, "Generates actionable scope change suggestions from elicitation rounds",
                        opus4Primary, AgentOutputFormat.JSON)).id());

        return ids;
    }

    // ── Prompt Templates + Versions ───────────────────────────────────────────

    private void seedPromptTemplatesAndVersions(Map<String, UUID> agentIds) {
        seedTemplate(agentIds.get("PROJECT_BRIEF_AGENT"),
                "PROJECT_BRIEF_GENERATION_V1", "Project Brief Generation v1",
                "Generates a structured project brief JSON from project metadata and scope snapshot",
                PROJECT_BRIEF_SYSTEM, PROJECT_BRIEF_USER,
                PROJECT_BRIEF_VARIABLE_SCHEMA, PROJECT_BRIEF_RESPONSE_SCHEMA,
                new BigDecimal("0.3"), 4096);

        seedTemplate(agentIds.get("KNOWLEDGE_QUERY_REWRITER_AGENT"),
                "KNOWLEDGE_QUERY_REWRITER_V1", "Knowledge Query Rewriter v1",
                "Rewrites a natural language query into optimized semantic search keywords",
                QUERY_REWRITER_SYSTEM, QUERY_REWRITER_USER,
                QUERY_REWRITER_VARIABLE_SCHEMA, null,
                new BigDecimal("0.1"), 256);

        seedTemplate(agentIds.get("SPEC_PACK_OUTLINE_AGENT"),
                "SPEC_PACK_OUTLINE_V1", "Spec Pack Outline v1",
                "Generates a structured document outline from scope requirements, functions, and use cases",
                SPEC_OUTLINE_SYSTEM, SPEC_OUTLINE_USER,
                SPEC_OUTLINE_VARIABLE_SCHEMA, SPEC_OUTLINE_RESPONSE_SCHEMA,
                new BigDecimal("0.3"), 4096);

        seedTemplate(agentIds.get("SPEC_PACK_SECTION_AGENT"),
                "SPEC_PACK_SECTION_V1", "Spec Pack Section Generator v1",
                "Generates content blocks for a single specification section",
                SPEC_SECTION_SYSTEM, SPEC_SECTION_USER,
                SPEC_SECTION_VARIABLE_SCHEMA, SPEC_SECTION_RESPONSE_SCHEMA,
                new BigDecimal("0.4"), 4096);

        seedTemplate(agentIds.get("ELICITATION_QUESTION_AGENT"),
                "ELICITATION_GENERATE_QUESTIONS_V1", "Elicitation Question Generator v1",
                "Generates clarification questions from scope entities to surface ambiguities",
                ELICIT_QUESTIONS_SYSTEM, ELICIT_QUESTIONS_USER,
                ELICIT_QUESTIONS_VARIABLE_SCHEMA, ELICIT_QUESTIONS_RESPONSE_SCHEMA,
                new BigDecimal("0.2"), 8192);

        seedTemplate(agentIds.get("ELICITATION_EVALUATOR_AGENT"),
                "ELICITATION_EVALUATE_ANSWERS_V1", "Elicitation Answer Evaluator v1",
                "Evaluates answers and assigns 5-level clarity scores (BLOCKED → CLEARED)",
                ELICIT_EVALUATE_SYSTEM, ELICIT_EVALUATE_USER,
                ELICIT_EVALUATE_VARIABLE_SCHEMA, ELICIT_EVALUATE_RESPONSE_SCHEMA,
                new BigDecimal("0.1"), 8192);

        seedTemplate(agentIds.get("ELICITATION_SUGGESTION_AGENT"),
                "ELICITATION_GENERATE_SUGGESTIONS_V1", "Elicitation Suggestion Generator v1",
                "Generates actionable scope improvement suggestions from a completed elicitation round",
                ELICIT_SUGGEST_SYSTEM, ELICIT_SUGGEST_USER,
                ELICIT_SUGGEST_VARIABLE_SCHEMA, ELICIT_SUGGEST_RESPONSE_SCHEMA,
                new BigDecimal("0.2"), 8192);
    }

    private void seedTemplate(UUID agentId, String templateCode, String templateName,
                               String templateDesc, String systemPrompt, String userPromptTemplate,
                               String variableSchema, String responseSchemaJson,
                               BigDecimal temperature, Integer maxTokens) {
        if (agentId == null) return;
        PromptTemplateCode code = PromptTemplateCode.of(templateCode);
        if (promptTemplateRepository.existsByAgentIdAndCode(agentId, code)) return;

        PromptTemplate template = promptTemplateRepository.save(
                PromptTemplate.create(agentId, templateName, code, templateDesc));

        int nextVersion = promptVersionRepository.getMaxVersionNumber(template.id()) + 1;
        PromptVersion version = PromptVersion.create(
                template.id(), nextVersion, templateName + " — Initial",
                userPromptTemplate, PromptContentFormat.TEXT,
                variableSchema, "Initial seed version",
                systemPrompt, userPromptTemplate,
                "json_object", responseSchemaJson,
                temperature, null, maxTokens);
        version.activate("SYSTEM");
        promptVersionRepository.save(version);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Prompt content constants
    // ═══════════════════════════════════════════════════════════════════════════

    // ── Project Brief Generation ──────────────────────────────────────────────

    private static final String PROJECT_BRIEF_SYSTEM = """
            You are a technical project brief generator for software projects.
            Your role is to produce a structured JSON brief from project metadata and scope information.
            Rules:
            - Output only valid JSON matching the requested schema.
            - Be concise and technical. No filler text.
            - Infer missing details from the available context.
            - Dates in ISO-8601 format. Status in UPPERCASE.
            """;

    private static final String PROJECT_BRIEF_USER = """
            Generate a project brief for the following project.

            Project Name: {{PROJECT_NAME}}
            Project Code: {{PROJECT_CODE}}
            Status: {{PROJECT_STATUS}}
            Description: {{PROJECT_DESCRIPTION}}
            Start Date: {{START_DATE}}
            End Date: {{END_DATE}}

            Scope Snapshot:
            {{PROJECT_SNAPSHOT}}

            Return a JSON object with:
            - title, code, status, description, startDate, endDate
            - summary (2-3 sentence executive summary)
            - objectives (array of strings)
            - scopeHighlights (array of { area, description })
            - risks (array of { risk, mitigation })
            - keyMetrics (array of { metric, value })
            """;

    private static final String PROJECT_BRIEF_VARIABLE_SCHEMA = """
            {
              "variables": [
                {"name":"PROJECT_NAME","description":"Full project name","format":"Plain text","syntax":"{{PROJECT_NAME}}"},
                {"name":"PROJECT_CODE","description":"Unique project code identifier","format":"Uppercase string","syntax":"{{PROJECT_CODE}}"},
                {"name":"PROJECT_STATUS","description":"Current lifecycle status of the project","format":"UPPERCASE enum: DRAFT, ACTIVE, COMPLETED, ARCHIVED","syntax":"{{PROJECT_STATUS}}"},
                {"name":"PROJECT_DESCRIPTION","description":"Short project description from project settings","format":"Plain text","syntax":"{{PROJECT_DESCRIPTION}}"},
                {"name":"START_DATE","description":"Project start date","format":"ISO-8601 date or null","syntax":"{{START_DATE}}"},
                {"name":"END_DATE","description":"Project target end date","format":"ISO-8601 date or null","syntax":"{{END_DATE}}"},
                {"name":"PROJECT_SNAPSHOT","description":"JSON snapshot of requirements, functions, use cases, and entities in the project scope","format":"JSON object","syntax":"{{PROJECT_SNAPSHOT}}"}
              ]
            }
            """;

    private static final String PROJECT_BRIEF_RESPONSE_SCHEMA = """
            {
              "type":"object",
              "required":["title","code","status","summary","objectives","scopeHighlights"],
              "properties":{
                "title":{"type":"string"},
                "code":{"type":"string"},
                "status":{"type":"string"},
                "description":{"type":"string"},
                "startDate":{"type":"string"},
                "endDate":{"type":"string"},
                "summary":{"type":"string"},
                "objectives":{"type":"array","items":{"type":"string"}},
                "scopeHighlights":{"type":"array","items":{"type":"object","properties":{"area":{"type":"string"},"description":{"type":"string"}}}},
                "risks":{"type":"array","items":{"type":"object","properties":{"risk":{"type":"string"},"mitigation":{"type":"string"}}}},
                "keyMetrics":{"type":"array","items":{"type":"object","properties":{"metric":{"type":"string"},"value":{"type":"string"}}}}
              }
            }
            """;

    // ── Knowledge Query Rewriter ──────────────────────────────────────────────

    private static final String QUERY_REWRITER_SYSTEM = """
            You are a search query optimizer.
            Extract the most relevant technical keywords from the user's question for semantic vector search.
            Rules:
            - Return 3-8 keywords or short phrases separated by commas.
            - Remove stop words, greetings, and filler.
            - Preserve technical terms exactly.
            - No full sentences. No punctuation other than commas.
            """;

    private static final String QUERY_REWRITER_USER = """
            Rewrite the following query into optimized semantic search keywords:

            {{QUERY}}
            """;

    private static final String QUERY_REWRITER_VARIABLE_SCHEMA = """
            {
              "variables": [
                {"name":"QUERY","description":"The raw user search query to be rewritten","format":"Natural language string","syntax":"{{QUERY}}"}
              ]
            }
            """;

    // ── Spec Pack Outline ─────────────────────────────────────────────────────

    private static final String SPEC_OUTLINE_SYSTEM = """
            You are a software specification document architect.
            Generate structured outlines for developer implementation spec packs.
            Rules:
            - Output only valid JSON matching the requested schema.
            - Each section must have a unique key (snake_case), a clear title, description, and expected block types.
            - Order sections logically: overview first, then feature sections, then technical appendices.
            - Keep section count between 5 and 15.
            - Block types: SUMMARY, TEXT, KEY_VALUE, TABLE, SCREEN_SPEC, FLOW, DIAGRAM, BUSINESS_RULES, ACCEPTANCE_CRITERIA, OPEN_QUESTIONS, TRACEABILITY_BLOCK
            """;

    private static final String SPEC_OUTLINE_USER = """
            Generate a specification document outline for the following scope.

            Scope Name: {{SCOPE_NAME}}

            Requirements:
            {{REQUIREMENTS_JSON}}

            Functions:
            {{FUNCTIONS_JSON}}

            Use Cases:
            {{USE_CASES_JSON}}

            Answered Clarifications:
            {{ANSWERED_CLARIFICATIONS_JSON}}

            Return a JSON object with:
            - sections: array of { key, title, description, blockTypes[] }
            """;

    private static final String SPEC_OUTLINE_VARIABLE_SCHEMA = """
            {
              "variables": [
                {"name":"SCOPE_NAME","description":"Name of the scope package being documented","format":"Plain text","syntax":"{{SCOPE_NAME}}"},
                {"name":"REQUIREMENTS_JSON","description":"JSON array of requirements in scope. Each: {id, title, description, priority}","format":"JSON array","syntax":"{{REQUIREMENTS_JSON}}"},
                {"name":"FUNCTIONS_JSON","description":"JSON array of functional items. Each: {id, title, description}","format":"JSON array","syntax":"{{FUNCTIONS_JSON}}"},
                {"name":"USE_CASES_JSON","description":"JSON array of use cases. Each: {id, title, description, actor}","format":"JSON array","syntax":"{{USE_CASES_JSON}}"},
                {"name":"ANSWERED_CLARIFICATIONS_JSON","description":"JSON array of answered clarification Q&A. Each: {question, answer, priority}","format":"JSON array","syntax":"{{ANSWERED_CLARIFICATIONS_JSON}}"}
              ]
            }
            """;

    private static final String SPEC_OUTLINE_RESPONSE_SCHEMA = """
            {
              "type":"object",
              "required":["sections"],
              "properties":{
                "sections":{
                  "type":"array",
                  "items":{
                    "type":"object",
                    "required":["key","title","description","blockTypes"],
                    "properties":{
                      "key":{"type":"string"},
                      "title":{"type":"string"},
                      "description":{"type":"string"},
                      "blockTypes":{"type":"array","items":{"type":"string"}}
                    }
                  }
                }
              }
            }
            """;

    // ── Spec Pack Section ─────────────────────────────────────────────────────

    private static final String SPEC_SECTION_SYSTEM = """
            You are a software specification writer for developer implementation documents.
            Generate content blocks for a single document section.
            Rules:
            - Output only valid JSON matching the requested schema.
            - Each block must have: blockKey (snake_case unique), blockType, title, contentFormat, contentJson.
            - Use contentFormat: MARKDOWN for text, STRUCTURED for key-value, STRUCTURED_TABLE for tables, JSON for data.
            - Be specific and actionable. Avoid vague statements.
            - Reference requirement/function IDs when relevant.
            """;

    private static final String SPEC_SECTION_USER = """
            Generate content blocks for the following specification section.

            Section Title: {{SECTION_TITLE}}
            Section Description: {{SECTION_DESCRIPTION}}
            Expected Block Types: {{EXPECTED_BLOCK_TYPES}}

            Scope Context:
            {{SCOPE_CONTEXT_JSON}}

            Previously Generated Sections:
            {{PREVIOUS_BLOCKS_JSON}}

            Return a JSON object with:
            - blocks: array of { blockKey, blockType, title, contentFormat, contentJson, sequence }
            """;

    private static final String SPEC_SECTION_VARIABLE_SCHEMA = """
            {
              "variables": [
                {"name":"SECTION_TITLE","description":"Title of the section to generate content for","format":"Plain text","syntax":"{{SECTION_TITLE}}"},
                {"name":"SECTION_DESCRIPTION","description":"What this section should cover","format":"Plain text","syntax":"{{SECTION_DESCRIPTION}}"},
                {"name":"EXPECTED_BLOCK_TYPES","description":"Comma-separated block types expected in this section","format":"CSV of block type names","syntax":"{{EXPECTED_BLOCK_TYPES}}"},
                {"name":"SCOPE_CONTEXT_JSON","description":"Full scope context: requirements, functions, use cases, screens, APIs, entities","format":"JSON object","syntax":"{{SCOPE_CONTEXT_JSON}}"},
                {"name":"PREVIOUS_BLOCKS_JSON","description":"Array of already-generated blocks from previous sections (last 2-3 sections for context)","format":"JSON array","syntax":"{{PREVIOUS_BLOCKS_JSON}}"}
              ]
            }
            """;

    private static final String SPEC_SECTION_RESPONSE_SCHEMA = """
            {
              "type":"object",
              "required":["blocks"],
              "properties":{
                "blocks":{
                  "type":"array",
                  "items":{
                    "type":"object",
                    "required":["blockKey","blockType","title","contentFormat","contentJson","sequence"],
                    "properties":{
                      "blockKey":{"type":"string"},
                      "blockType":{"type":"string"},
                      "title":{"type":"string"},
                      "contentFormat":{"type":"string"},
                      "contentJson":{"type":"string"},
                      "sequence":{"type":"integer"}
                    }
                  }
                }
              }
            }
            """;

    // ── Elicitation: Generate Questions ──────────────────────────────────────

    private static final String ELICIT_QUESTIONS_SYSTEM = """
            You are a requirements elicitation specialist for software projects.
            Analyze the provided scope and generate targeted clarification questions to surface ambiguities.
            Rules:
            - Output only valid JSON matching the requested schema.
            - Generate 5-15 questions ordered by importance (most critical first).
            - Cover: unclear requirements, missing acceptance criteria, ambiguous business rules, technical dependencies, edge cases.
            - Each question must be specific and answerable. No vague questions.
            - Assign a category to each question: REQUIREMENT, FUNCTION, SCREEN, API, ENTITY, COMPONENT, BUSINESS_RULE
            """;

    private static final String ELICIT_QUESTIONS_USER = """
            Generate clarification questions for the following scope to reduce ambiguity before specification writing.

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
            - questions: array of { sequence, questionText, category }
            """;

    private static final String ELICIT_QUESTIONS_VARIABLE_SCHEMA = """
            {
              "variables": [
                {"name":"SCOPE_NAME","description":"Name of the scope package being analyzed","format":"Plain text","syntax":"{{SCOPE_NAME}}"},
                {"name":"REQUIREMENTS_JSON","description":"JSON array of requirements. Each: {id, title, description, priority}","format":"JSON array","syntax":"{{REQUIREMENTS_JSON}}"},
                {"name":"FUNCTIONS_JSON","description":"JSON array of functions/features. Each: {id, title, description}","format":"JSON array","syntax":"{{FUNCTIONS_JSON}}"},
                {"name":"USE_CASES_JSON","description":"JSON array of use cases. Each: {id, title, description, actor, preconditions, postconditions}","format":"JSON array","syntax":"{{USE_CASES_JSON}}"},
                {"name":"SCREENS_JSON","description":"JSON array of UI screens. Each: {id, title, description}","format":"JSON array","syntax":"{{SCREENS_JSON}}"},
                {"name":"APIS_JSON","description":"JSON array of API endpoints. Each: {id, title, method, path, description}","format":"JSON array","syntax":"{{APIS_JSON}}"},
                {"name":"ENTITIES_JSON","description":"JSON array of data entities. Each: {id, title, description}","format":"JSON array","syntax":"{{ENTITIES_JSON}}"},
                {"name":"COMPONENTS_JSON","description":"JSON array of components. Each: {id, title, description}","format":"JSON array","syntax":"{{COMPONENTS_JSON}}"}
              ]
            }
            """;

    private static final String ELICIT_QUESTIONS_RESPONSE_SCHEMA = """
            {
              "type":"object",
              "required":["questions"],
              "properties":{
                "questions":{
                  "type":"array",
                  "items":{
                    "type":"object",
                    "required":["sequence","questionText","category"],
                    "properties":{
                      "sequence":{"type":"integer"},
                      "questionText":{"type":"string"},
                      "category":{"type":"string","enum":["REQUIREMENT","FUNCTION","SCREEN","API","ENTITY","COMPONENT","BUSINESS_RULE"]}
                    }
                  }
                }
              }
            }
            """;

    // ── Elicitation: Evaluate Answers ─────────────────────────────────────────

    private static final String ELICIT_EVALUATE_SYSTEM = """
            You are a requirements clarity evaluator for software projects.
            Evaluate each answer and assign a clarity level based on how well it resolves the original question.

            Clarity levels (5 levels, assign exactly one):
            - BLOCKED: Answer is missing or contradictory. Cannot proceed. Must be resolved before spec writing.
            - CRITICAL: Answer is vague or incomplete on a critical aspect. Significant risk if unresolved.
            - IMPORTANT: Answer partially addresses the question. Some follow-up needed.
            - MINOR: Answer is mostly clear with minor gaps. Low risk.
            - CLEARED: Answer fully resolves the question. No follow-up needed.

            Rules:
            - Output only valid JSON matching the requested schema.
            - For any level other than CLEARED, provide a specific follow-up question as feedback.
            - CLEARED means no feedback needed (feedback: null).
            - Be strict: a vague answer on a critical question is BLOCKED or CRITICAL.
            """;

    private static final String ELICIT_EVALUATE_USER = """
            Evaluate the following answers for the scope context below.

            Scope Context:
            {{SCOPE_CONTEXT_JSON}}

            Questions and Answers:
            {{QUESTIONS_AND_ANSWERS_JSON}}

            Return a JSON object with:
            - evaluations: array of { questionId, clarityLevel, feedback }
            """;

    private static final String ELICIT_EVALUATE_VARIABLE_SCHEMA = """
            {
              "variables": [
                {"name":"SCOPE_CONTEXT_JSON","description":"Scope context including requirements, functions, use cases, screens, APIs, entities","format":"JSON object","syntax":"{{SCOPE_CONTEXT_JSON}}"},
                {"name":"QUESTIONS_AND_ANSWERS_JSON","description":"JSON array of question-answer pairs. Each: {questionId, questionText, answerText}","format":"JSON array","syntax":"{{QUESTIONS_AND_ANSWERS_JSON}}"}
              ]
            }
            """;

    private static final String ELICIT_EVALUATE_RESPONSE_SCHEMA = """
            {
              "type":"object",
              "required":["evaluations"],
              "properties":{
                "evaluations":{
                  "type":"array",
                  "items":{
                    "type":"object",
                    "required":["questionId","clarityLevel"],
                    "properties":{
                      "questionId":{"type":"string"},
                      "clarityLevel":{"type":"string","enum":["BLOCKED","CRITICAL","IMPORTANT","MINOR","CLEARED"]},
                      "feedback":{"type":["string","null"]}
                    }
                  }
                }
              }
            }
            """;

    // ── Elicitation: Generate Suggestions ────────────────────────────────────

    private static final String ELICIT_SUGGEST_SYSTEM = """
            You are a requirements improvement specialist for software projects.
            Based on a completed elicitation round, generate actionable scope change suggestions.

            Suggestion actions available:
            UPDATE_REQUIREMENT, CREATE_REQUIREMENT, DELETE_REQUIREMENT,
            UPDATE_FUNCTION, CREATE_FUNCTION, DELETE_FUNCTION,
            UPDATE_USE_CASE, CREATE_USE_CASE, DELETE_USE_CASE,
            UPDATE_SCREEN, CREATE_SCREEN, UPDATE_API, CREATE_API,
            UPDATE_ENTITY, LINK_REQUIREMENT_FUNCTION, UNLINK_REQUIREMENT_FUNCTION

            Rules:
            - Output only valid JSON matching the requested schema.
            - Each suggestion item must have a clear rationale linked to specific Q&A findings.
            - Prioritize: fix BLOCKED/CRITICAL clarity issues first, then IMPORTANT.
            - Include precondition actions when order matters (e.g., unlink before delete).
            - Estimate impact: LOW / MEDIUM / HIGH based on scope change size.
            - Be specific: for updates, state exactly what field to change and to what value.
            """;

    private static final String ELICIT_SUGGEST_USER = """
            Generate scope improvement suggestions based on the completed elicitation round.

            Scope Context:
            {{SCOPE_CONTEXT_JSON}}

            Elicitation Round (Q&A with clarity evaluations):
            {{ELICITATION_ROUND_JSON}}

            Return a JSON object with:
            - overallSummary: string summarizing key findings and recommendations
            - suggestions: array of {
                sequence, action, targetEntityType, targetEntityId, targetEntityName,
                rationale, changesJson, preconditionActionsJson, estimatedImpact
              }
            """;

    private static final String ELICIT_SUGGEST_VARIABLE_SCHEMA = """
            {
              "variables": [
                {"name":"SCOPE_CONTEXT_JSON","description":"Full scope context: requirements, functions, use cases, screens, APIs, entities, components","format":"JSON object","syntax":"{{SCOPE_CONTEXT_JSON}}"},
                {"name":"ELICITATION_ROUND_JSON","description":"Complete elicitation round snapshot with all Q&A and clarity evaluations. Each question: {questionId, questionText, answerText, clarityLevel, aiFeedback}","format":"JSON object","syntax":"{{ELICITATION_ROUND_JSON}}"}
              ]
            }
            """;

    private static final String ELICIT_SUGGEST_RESPONSE_SCHEMA = """
            {
              "type":"object",
              "required":["overallSummary","suggestions"],
              "properties":{
                "overallSummary":{"type":"string"},
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
                }
              }
            }
            """;
}
