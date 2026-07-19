package com.company.scopery.modules.aiplanning.shared.constant;

import java.util.List;

public final class AiPlanningPromptCodes {
    public static final String PROJECT_PLAN_DRAFT_PROMPT = "PROJECT_PLAN_DRAFT_PROMPT";
    public static final String PROJECT_TEMPLATE_RECOMMENDATION_PROMPT = "PROJECT_TEMPLATE_RECOMMENDATION_PROMPT";
    public static final String TASK_ESTIMATE_SUGGESTION_PROMPT = "TASK_ESTIMATE_SUGGESTION_PROMPT";
    public static final String COST_ROLE_SUGGESTION_PROMPT = "COST_ROLE_SUGGESTION_PROMPT";
    public static final String SCHEDULE_RISK_EXPLANATION_PROMPT = "SCHEDULE_RISK_EXPLANATION_PROMPT";
    public static final String FINANCE_INSIGHT_PROMPT = "FINANCE_INSIGHT_PROMPT";
    public static final String QUOTE_PROPOSAL_DRAFT_PROMPT = "QUOTE_PROPOSAL_DRAFT_PROMPT";
    public static final String CHANGE_REQUEST_DRAFT_PROMPT = "CHANGE_REQUEST_DRAFT_PROMPT";

    public static List<String> all() {
        return List.of(
                PROJECT_PLAN_DRAFT_PROMPT,
                PROJECT_TEMPLATE_RECOMMENDATION_PROMPT,
                TASK_ESTIMATE_SUGGESTION_PROMPT,
                COST_ROLE_SUGGESTION_PROMPT,
                SCHEDULE_RISK_EXPLANATION_PROMPT,
                FINANCE_INSIGHT_PROMPT,
                QUOTE_PROPOSAL_DRAFT_PROMPT,
                CHANGE_REQUEST_DRAFT_PROMPT);
    }

    private AiPlanningPromptCodes() {}
}
