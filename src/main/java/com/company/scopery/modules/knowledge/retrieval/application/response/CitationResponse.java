package com.company.scopery.modules.knowledge.retrieval.application.response;

public record CitationResponse(
        String sourceId,
        String sourceType,
        String sourceRefId,
        String title,
        String appRoute,
        int chunkOrdinal
) {}
