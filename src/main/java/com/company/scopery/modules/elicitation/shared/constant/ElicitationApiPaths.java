package com.company.scopery.modules.elicitation.shared.constant;

public final class ElicitationApiPaths {

    public static final String BASE_SESSIONS    = "/api/v1/projects/{projectId}/elicitation-sessions";
    public static final String SESSION_BY_ID    = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}";
    public static final String SESSION_CLOSE    = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/close";
    public static final String SESSION_CANCEL   = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/cancel";

    public static final String SESSION_QUESTIONS  = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/questions";
    public static final String QUESTION_ANSWER    = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/questions/{questionId}/answer";
    public static final String QUESTION_SKIP      = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/questions/{questionId}/skip";

    public static final String SESSION_ACTIVE_LOCK   = "/api/v1/projects/{projectId}/elicitation-sessions/active-lock";
    public static final String SESSION_SCOPE_TREE    = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/scope-tree";

    public static final String SESSION_ROUNDS        = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/rounds";
    public static final String SESSION_ROUNDS_GENERATE = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/rounds/generate";
    public static final String ROUND_BY_ID           = "/api/v1/elicitation-rounds/{roundId}";
    public static final String ROUND_SUBMIT          = "/api/v1/projects/{projectId}/elicitation-sessions/{sessionId}/rounds/{roundId}/submit";

    public static final String ROUND_SUGGESTIONS        = "/api/v1/elicitation-rounds/{roundId}/suggestions";
    public static final String SUGGESTION_ITEM_APPROVE  = "/api/v1/elicitation-suggestion-items/{itemId}/approve";
    public static final String SUGGESTION_ITEM_REJECT   = "/api/v1/elicitation-suggestion-items/{itemId}/reject";

    private ElicitationApiPaths() {}
}
