package com.company.scopery.modules.aiplanning.shared.constant;

/**
 * Table names for AI Planning (Flyway V58).
 * <p>Known deviation from CLAUDE.md §12 {@code {module}_} prefix ({@code aiplanning_*}):
 * V58 baked {@code ai_planning_*} names. Do not rename without a dedicated migration.
 */
public final class AiPlanningTableNames {
    public static final String PLANNING_RUN = "ai_planning_run";
    public static final String CONTEXT_SNAPSHOT = "ai_planning_context_snapshot";
    public static final String SUGGESTION = "ai_planning_suggestion";
    public static final String SUGGESTION_ITEM = "ai_planning_suggestion_item";
    public static final String REVIEW_ACTION = "ai_planning_review_action";
    public static final String APPLY_RESULT = "ai_planning_apply_result";
    private AiPlanningTableNames() {}
}
