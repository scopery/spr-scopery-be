package com.company.scopery.modules.knowledge.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class KnowledgeApiPaths {

    private static final String BASE = ApiPaths.BASE_PATH + "/knowledge";

    public static final String DOCUMENT_TYPES = BASE + "/document-types";
    public static final String DOCUMENT_CLASSIFICATIONS = BASE + "/document-classifications";

    // Phase 41 — semantic retrieval
    public static final String SOURCES = BASE + "/sources";
    public static final String INDEXING = BASE + "/indexing";
    public static final String RETRIEVAL = BASE + "/retrieval";
    public static final String GRAPH = BASE + "/graph";

    private KnowledgeApiPaths() {}
}
