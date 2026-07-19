package com.company.scopery.modules.aiplanning.planningrun.application.service;

import com.company.scopery.modules.aiplanning.planningrun.domain.enums.PlanningRunType;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionType;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemOperation;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemType;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AiPlanningSuggestionGenerator {

    public Generated generate(UUID planningRunId, UUID projectId, UUID workspaceId, PlanningRunType runType,
                              Map<String, Object> input) {
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
        return new Generated(suggestion, items, "{\"title\":\"Suggested plan\",\"itemCount\":3}");
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
        return new Generated(suggestion, items, "{\"itemCount\":1}");
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
        return new Generated(suggestion, items, "{\"itemCount\":1}");
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
        return new Generated(suggestion, items, "{\"itemCount\":1}");
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
        return new Generated(suggestion, items, "{\"itemCount\":1}");
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
        return new Generated(suggestion, items, "{\"itemCount\":1}");
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
        return new Generated(suggestion, items, "{\"itemCount\":1}");
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
        return new Generated(suggestion, items, "{\"itemCount\":1}");
    }

    private Generated general(UUID runId, UUID projectId, UUID workspaceId) {
        AiPlanningSuggestion suggestion = AiPlanningSuggestion.create(
                runId, projectId, workspaceId, SuggestionType.MIXED_PLAN,
                "General project assistant notes", "General planning notes",
                "Proposal only", "LOW", null);
        return new Generated(suggestion, List.of(), "{\"itemCount\":0}");
    }

    public record Generated(AiPlanningSuggestion suggestion, List<AiPlanningSuggestionItem> items, String outputSummaryJson) {}
}
