package com.company.scopery.modules.quote.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class QuoteApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects";

    public static final String QUOTES = BASE + "/{projectId}/quotes";
    public static final String QUOTE_VERSIONS = QUOTES + "/{quoteId}/versions";
    public static final String QUOTE_LINES = QUOTE_VERSIONS + "/{versionId}/lines";
    public static final String QUOTE_TERMS = QUOTE_VERSIONS + "/{versionId}/terms";
    public static final String QUOTE_SUMMARY = QUOTE_VERSIONS + "/{versionId}/summary";

    private QuoteApiPaths() {}
}
