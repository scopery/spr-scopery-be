package com.company.scopery.modules.aiplanning.planningrun.application.service;

import com.company.scopery.integration.ai.anthropic.AnthropicClient;
import com.company.scopery.integration.ai.anthropic.AnthropicProperties;
import com.company.scopery.integration.ai.anthropic.AnthropicRequest;
import com.company.scopery.integration.ai.anthropic.AnthropicResponse;
import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunType;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionType;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemOperation;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemType;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AiPlanningSuggestionGenerator {

    private static final Logger log = LoggerFactory.getLogger(AiPlanningSuggestionGenerator.class);

    private static final String SYSTEM_PROMPT = """
            You are an expert IT project planning assistant for a consulting project management system.
            Analyze the project context and generate actionable planning suggestions.

            Return ONLY a valid JSON object with this exact structure (no markdown, no explanation):
            {
              "title": "Brief title for the suggestion group",
              "rationale": "Why these suggestions are made",
              "confidence": "HIGH|MEDIUM|LOW",
              "items": [
                {
                  "type": "WBS_NODE|TASK|RISK|SCHEDULE_ADJUSTMENT|COST_ROLE|TASK_ESTIMATE|FINANCE_NOTE|QUOTE_TERM|CHANGE_REQUEST",
                  "operation": "CREATE|UPDATE|RECOMMEND|DRAFT_TEXT",
                  "title": "Item title",
                  "description": "Detailed description",
                  "payloadJson": "{}",
                  "rationale": "Why this item",
                  "confidence": "HIGH|MEDIUM|LOW",
                  "targetEntityType": "WBS_NODE|TASK|RISK|COST_ROLE|FINANCE|QUOTE_TERM|CHANGE_REQUEST|TEMPLATE"
                }
              ]
            }

            Rules:
            - Return 3-8 actionable, specific items based on the actual project data provided
            - payloadJson must be a JSON-encoded string (not a nested object)
            - Do not include salary amounts, API keys, or any sensitive data
            - Focus on delivery risk and project quality improvement
            """;

    private final AnthropicClient anthropicClient;
    private final AnthropicProperties anthropicProperties;
    private final ObjectMapper objectMapper;

    public AiPlanningSuggestionGenerator(AnthropicClient anthropicClient,
                                          AnthropicProperties anthropicProperties,
                                          ObjectMapper objectMapper) {
        this.anthropicClient = anthropicClient;
        this.anthropicProperties = anthropicProperties;
        this.objectMapper = objectMapper;
    }

    public Generated generate(UUID planningRunId, UUID projectId, UUID workspaceId, PlanningRunType runType,
                              Map<String, Object> input, String contextPayloadJson) {
        String apiKey = anthropicProperties.apiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Anthropic API key not configured — using stub for runType={}", runType);
            return stub(planningRunId, projectId, workspaceId, runType, input);
        }
        try {
            return callAi(planningRunId, projectId, workspaceId, runType, input, contextPayloadJson, apiKey);
        } catch (Exception e) {
            log.warn("AI planning generation failed for runType={}: {}", runType, e.getMessage());
            return stub(planningRunId, projectId, workspaceId, runType, input);
        }
    }

    private Generated callAi(UUID planningRunId, UUID projectId, UUID workspaceId, PlanningRunType runType,
                               Map<String, Object> input, String contextPayloadJson, String apiKey) throws Exception {
        String taskDescription = switch (runType) {
            case PROJECT_PLAN_DRAFT ->
                    "Create a draft project plan: suggest WBS nodes, key tasks, and risk items based on the actual project data.";
            case TASK_ESTIMATE_SUGGESTION ->
                    "Suggest estimate hours for tasks that seem to be missing estimates or have estimates that look unrealistic.";
            case COST_ROLE_SUGGESTION ->
                    "Suggest appropriate cost roles for tasks based on their complexity, title, and type.";
            case SCHEDULE_RISK_EXPLANATION ->
                    "Identify schedule risks based on the current tasks and phases, and suggest mitigation strategies.";
            case FINANCE_INSIGHT ->
                    "Provide high-level finance insights about cost drivers and budget risk areas. Do not expose salary data.";
            case QUOTE_PROPOSAL_DRAFT ->
                    "Draft commercial proposal text based on project scope.";
            case CHANGE_REQUEST_DRAFT ->
                    "Draft a change request based on the variance notes provided.";
            case TEMPLATE_RECOMMENDATION ->
                    "Recommend suitable project structures based on the project type and scope.";
            default ->
                    "Provide general project planning suggestions based on the project context.";
        };

        String inputJson = "{}";
        if (input != null && !input.isEmpty()) {
            try { inputJson = objectMapper.writeValueAsString(input); } catch (Exception ignored) {}
        }

        String userMessage = "Task: " + taskDescription + "\n\n"
                + "Project context:\n" + (contextPayloadJson != null ? contextPayloadJson : "{}") + "\n\n"
                + ("{}".equals(inputJson) ? "" : "Additional input:\n" + inputJson);

        AnthropicRequest request = new AnthropicRequest(
                "claude-sonnet-4-6",
                SYSTEM_PROMPT,
                List.of(Map.of("role", "user", "content", userMessage)),
                4096,
                new BigDecimal("0.3"),
                false
        );

        AnthropicResponse response = anthropicClient.callMessages(apiKey, request);
        String rawText = response.extractOutputText().trim();

        if (rawText.startsWith("```json")) rawText = rawText.substring(7).trim();
        else if (rawText.startsWith("```")) rawText = rawText.substring(3).trim();
        if (rawText.endsWith("```")) rawText = rawText.substring(0, rawText.length() - 3).trim();

        return parseAiResponse(planningRunId, projectId, workspaceId, runType, rawText);
    }

    @SuppressWarnings("unchecked")
    private Generated parseAiResponse(UUID planningRunId, UUID projectId, UUID workspaceId,
                                       PlanningRunType runType, String rawText) throws Exception {
        Map<String, Object> parsed = objectMapper.readValue(rawText, Map.class);

        String title = String.valueOf(parsed.getOrDefault("title", "AI Planning Suggestions"));
        String rationale = String.valueOf(parsed.getOrDefault("rationale", "Generated by AI analysis"));
        String confidence = String.valueOf(parsed.getOrDefault("confidence", "MEDIUM"));

        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                planningRunId, projectId, workspaceId, toSuggestionType(runType),
                title, title, rationale, confidence, null);

        List<AiPlanningSuggestionItem> items = new ArrayList<>();
        List<Map<String, Object>> rawItems =
                (List<Map<String, Object>>) parsed.getOrDefault("items", List.of());
        for (Map<String, Object> rawItem : rawItems) {
            SuggestionItemType type = parseEnum(SuggestionItemType.class,
                    String.valueOf(rawItem.getOrDefault("type", "TASK")), SuggestionItemType.TASK);
            SuggestionItemOperation operation = parseEnum(SuggestionItemOperation.class,
                    String.valueOf(rawItem.getOrDefault("operation", "RECOMMEND")), SuggestionItemOperation.RECOMMEND);
            String itemTitle = String.valueOf(rawItem.getOrDefault("title", "Suggestion"));
            String description = String.valueOf(rawItem.getOrDefault("description", ""));
            String payloadJson = String.valueOf(rawItem.getOrDefault("payloadJson", "{}"));
            String itemRationale = String.valueOf(rawItem.getOrDefault("rationale", ""));
            String itemConfidence = String.valueOf(rawItem.getOrDefault("confidence", "MEDIUM"));
            String targetEntityType = String.valueOf(rawItem.getOrDefault("targetEntityType", type.name()));

            items.add(AiPlanningSuggestionItem.create(
                    suggestion.id(), projectId, type, operation,
                    itemTitle, description, payloadJson, itemRationale, itemConfidence, targetEntityType, null));
        }

        String summaryJson = "{\"title\":\"" + escapeJson(title)
                + "\",\"itemCount\":" + items.size() + ",\"source\":\"ai\"}";
        return new Generated(suggestion, items, summaryJson);
    }

    private Generated stub(UUID planningRunId, UUID projectId, UUID workspaceId,
                           PlanningRunType runType, Map<String, Object> input) {
        return switch (runType) {
            case PROJECT_PLAN_DRAFT -> planDraft(planningRunId, projectId, workspaceId, input);
            case TASK_ESTIMATE_SUGGESTION -> estimate(planningRunId, projectId, workspaceId);
            case COST_ROLE_SUGGESTION -> costRole(planningRunId, projectId, workspaceId);
            case SCHEDULE_RISK_EXPLANATION -> scheduleRisk(planningRunId, projectId, workspaceId);
            case FINANCE_INSIGHT -> finance(planningRunId, projectId, workspaceId);
            case QUOTE_PROPOSAL_DRAFT -> quote(planningRunId, projectId, workspaceId);
            case CHANGE_REQUEST_DRAFT -> changeRequest(planningRunId, projectId, workspaceId);
            case TEMPLATE_RECOMMENDATION -> template(planningRunId, projectId, workspaceId);
            default -> general(planningRunId, projectId, workspaceId);
        };
    }

    private SuggestionType toSuggestionType(PlanningRunType runType) {
        return switch (runType) {
            case PROJECT_PLAN_DRAFT -> SuggestionType.MIXED_PLAN;
            case TASK_ESTIMATE_SUGGESTION -> SuggestionType.UPDATE_TASK_ESTIMATES;
            case COST_ROLE_SUGGESTION -> SuggestionType.UPDATE_COST_ROLES;
            case SCHEDULE_RISK_EXPLANATION -> SuggestionType.SCHEDULE_RISK_MITIGATION;
            case FINANCE_INSIGHT -> SuggestionType.FINANCE_INSIGHT;
            case QUOTE_PROPOSAL_DRAFT -> SuggestionType.QUOTE_TEXT_DRAFT;
            case CHANGE_REQUEST_DRAFT -> SuggestionType.CHANGE_REQUEST_DRAFT;
            case TEMPLATE_RECOMMENDATION -> SuggestionType.TEMPLATE_RECOMMENDATION;
            default -> SuggestionType.MIXED_PLAN;
        };
    }

    private <T extends Enum<T>> T parseEnum(Class<T> clazz, String value, T defaultValue) {
        try { return Enum.valueOf(clazz, value.toUpperCase()); }
        catch (Exception e) { return defaultValue; }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    // ── Stubs (fallback when API key is missing or call fails) ──────────────

    private Generated planDraft(UUID runId, UUID projectId, UUID workspaceId, Map<String, Object> input) {
        String goal = input == null ? "Project plan" : String.valueOf(input.getOrDefault("projectGoal", "Project plan"));
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.MIXED_PLAN,
                "Suggested plan: " + goal,
                "Draft WBS and tasks based on project goal. Proposal only — human approval required.",
                "Generated from project goal and structured context.",
                "MEDIUM", null);
        List<AiPlanningSuggestionItem> items = new ArrayList<>();
        items.add(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.WBS_NODE, SuggestionItemOperation.CREATE,
                "Discovery and Requirements", "Initial discovery WBS node",
                "{\"title\":\"Discovery and Requirements\",\"parentWbsNodeId\":null}",
                "Common first phase for delivery projects", "MEDIUM", "WBS_NODE", null));
        items.add(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.TASK, SuggestionItemOperation.CREATE,
                "Stakeholder interview workshop", "Collect requirements from stakeholders",
                "{\"title\":\"Stakeholder interview workshop\",\"estimateHours\":8}",
                "Helps refine scope before build", "MEDIUM", "TASK", null));
        items.add(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.RISK, SuggestionItemOperation.RECOMMEND,
                "Scope creep risk", "Ambiguous requirements may expand scope",
                "{\"severity\":\"MEDIUM\",\"mitigation\":\"Lock MVP scope with client\"}",
                "Based on typical portal delivery risks", "LOW", "RISK", null));
        return new Generated(suggestion, items, "{\"title\":\"Suggested plan\",\"itemCount\":3,\"source\":\"stub\"}");
    }

    private Generated estimate(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.UPDATE_TASK_ESTIMATES,
                "Task estimate suggestions", "Proposed estimateHours for incomplete tasks",
                "Heuristic proposal only", "LOW", null);
        List<AiPlanningSuggestionItem> items = List.of(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.TASK_ESTIMATE, SuggestionItemOperation.UPDATE,
                "Increase estimate for complex tasks", "Suggested baseline 16h for complex work",
                "{\"estimateHours\":16}", "Complexity heuristic", "LOW", "TASK", null));
        return new Generated(suggestion, items, "{\"itemCount\":1,\"source\":\"stub\"}");
    }

    private Generated costRole(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.UPDATE_COST_ROLES,
                "Cost role suggestions", "Suggested cost roles for unassigned work",
                "Does not expose salary", "MEDIUM", null);
        List<AiPlanningSuggestionItem> items = List.of(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.COST_ROLE, SuggestionItemOperation.RECOMMEND,
                "Assign SENIOR_DEV", "Recommend SENIOR_DEV cost role",
                "{\"costRoleCode\":\"SENIOR_DEV\"}", "Based on task complexity", "MEDIUM", "COST_ROLE", null));
        return new Generated(suggestion, items, "{\"itemCount\":1,\"source\":\"stub\"}");
    }

    private Generated scheduleRisk(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.SCHEDULE_RISK_MITIGATION,
                "Schedule risk explanation", "Mitigation proposals for schedule issues",
                "Does not reassign tasks automatically", "MEDIUM", null);
        List<AiPlanningSuggestionItem> items = List.of(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.SCHEDULE_ADJUSTMENT, SuggestionItemOperation.RECOMMEND,
                "Split overdue blocked work", "Consider splitting blocked tasks",
                "{\"action\":\"SPLIT_TASK\"}", "Capacity/schedule mitigation proposal", "MEDIUM", "TASK", null));
        return new Generated(suggestion, items, "{\"itemCount\":1,\"source\":\"stub\"}");
    }

    private Generated finance(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.FINANCE_INSIGHT,
                "Finance insight", "High-level margin/cost driver notes",
                "Insight only — no finance mutation", "MEDIUM", null);
        List<AiPlanningSuggestionItem> items = List.of(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.FINANCE_NOTE, SuggestionItemOperation.DRAFT_TEXT,
                "Review high-cost phases", "Check phases with high labor concentration",
                "{\"note\":\"Review high-cost phases before quote lock\"}",
                "Permission-gated finance insight", "MEDIUM", "FINANCE", null));
        return new Generated(suggestion, items, "{\"itemCount\":1,\"source\":\"stub\"}");
    }

    private Generated quote(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.QUOTE_TEXT_DRAFT,
                "Quote proposal draft", "Draft commercial wording",
                "Cannot set price/approve/send", "MEDIUM", null);
        List<AiPlanningSuggestionItem> items = List.of(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.QUOTE_TERM, SuggestionItemOperation.DRAFT_TEXT,
                "Executive summary draft", "Client-friendly summary draft",
                "{\"termType\":\"EXECUTIVE_SUMMARY\",\"content\":\"We propose an MVP delivery focused on core workflows.\"}",
                "Draft text only", "MEDIUM", "QUOTE_TERM", null));
        return new Generated(suggestion, items, "{\"itemCount\":1,\"source\":\"stub\"}");
    }

    private Generated changeRequest(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.CHANGE_REQUEST_DRAFT,
                "Change request draft", "Draft CR from variance notes",
                "Cannot submit/approve/apply CR", "MEDIUM", null);
        List<AiPlanningSuggestionItem> items = List.of(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.CHANGE_REQUEST, SuggestionItemOperation.DRAFT_TEXT,
                "Draft scope change", "Draft CR for scope adjustment",
                "{\"title\":\"Scope adjustment\",\"reason\":\"Client requested additional module\",\"changeType\":\"SCOPE\"}",
                "Draft only", "MEDIUM", "CHANGE_REQUEST", null));
        return new Generated(suggestion, items, "{\"itemCount\":1,\"source\":\"stub\"}");
    }

    private Generated template(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.TEMPLATE_RECOMMENDATION,
                "Template recommendation", "Recommended project template",
                "Cannot apply template automatically", "LOW", null);
        List<AiPlanningSuggestionItem> items = List.of(AiPlanningSuggestionItem.create(
                suggestion.id(), projectId, SuggestionItemType.WBS_NODE, SuggestionItemOperation.RECOMMEND,
                "Use standard delivery template", "Recommend published template if available",
                "{\"recommendation\":\"STANDARD_DELIVERY_TEMPLATE\"}",
                "Template catalog recommendation", "LOW", "TEMPLATE", null));
        return new Generated(suggestion, items, "{\"itemCount\":1,\"source\":\"stub\"}");
    }

    private Generated general(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.MIXED_PLAN,
                "General project assistant notes", "General planning notes",
                "Proposal only", "LOW", null);
        return new Generated(suggestion, List.of(), "{\"itemCount\":0,\"source\":\"stub\"}");
    }

    public record Generated(
            AiPlanningSuggestion suggestion,
            List<AiPlanningSuggestionItem> items,
            String outputSummaryJson) {}
}
