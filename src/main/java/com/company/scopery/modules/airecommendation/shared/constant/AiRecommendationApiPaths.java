package com.company.scopery.modules.airecommendation.shared.constant;

public final class AiRecommendationApiPaths {

    public static final String BASE                     = "/api/v1/ai-recommendations";
    public static final String PROJECT_RUNS             = BASE + "/projects/{projectId}/runs";
    public static final String RUN_BY_ID                = BASE + "/runs/{runId}";
    public static final String PROJECT_SUGGESTIONS      = BASE + "/projects/{projectId}/suggestions";
    public static final String ENTITY_SUGGESTIONS       = BASE + "/entities/{entityType}/{entityId}/suggestions";
    public static final String SUGGESTION_BY_REF        = BASE + "/suggestions/{suggestionRef}";
    public static final String SUGGESTION_VIEW          = BASE + "/suggestions/{suggestionRef}/view";
    public static final String SUGGESTION_EDIT          = BASE + "/suggestions/{suggestionRef}/edit";
    public static final String SUGGESTION_ACCEPT        = BASE + "/suggestions/{suggestionRef}/accept";
    public static final String SUGGESTION_REJECT        = BASE + "/suggestions/{suggestionRef}/reject";
    public static final String SUGGESTION_SUPPRESS      = BASE + "/suggestions/{suggestionRef}/suppress";
    public static final String SUGGESTION_FEEDBACK      = BASE + "/suggestions/{suggestionRef}/feedback";
    public static final String SUGGESTION_PREPARE_APPLY = BASE + "/suggestions/{suggestionRef}/prepare-apply";
    public static final String PROJECT_NEXT_BEST_ACTIONS = BASE + "/projects/{projectId}/next-best-actions";

    private AiRecommendationApiPaths() {}
}
