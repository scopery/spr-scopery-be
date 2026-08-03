package com.company.scopery.modules.traceability.aimapping.shared.constant;

public final class AiMappingApiPaths {

    public static final String SUGGESTIONS_BASE = "/api/projects/{projectId}/mapping-suggestions";
    public static final String SUGGESTIONS_GENERATE = "/api/projects/{projectId}/mapping-suggestions/generate";
    public static final String SUGGESTIONS_REVIEW = "/api/projects/{projectId}/mapping-suggestions/review";
    public static final String DRAFTS_APPLY = "/api/projects/{projectId}/mapping-drafts/{draftId}/apply";
    public static final String RELATIONS_REMAP = "/api/projects/{projectId}/relations/remap";
    public static final String CANDIDATES_SEARCH = "/api/projects/{projectId}/mapping-candidates";

    private AiMappingApiPaths() {}
}
