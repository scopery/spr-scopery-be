package com.company.scopery.modules.knowledge.retrieval.application.response;

public record RetrievalResultItem(
        String chunkId,
        String content,
        double score,
        CitationResponse citation
) {}
