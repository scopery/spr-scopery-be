package com.company.scopery.modules.aicontext.resolution.application.response;

import java.util.List;
import java.util.UUID;

public record AiContextResolutionResult(
        UUID documentId,
        String contextText,
        int tokenCount,
        int blockCount,
        List<BlockCitation> citations,
        UUID auditId
) {
    public record BlockCitation(
            String blockId,
            UUID documentId,
            String documentTitle,
            String headingPath
    ) {}

    public static AiContextResolutionResult of(UUID documentId, String contextText,
                                                int tokenCount, int blockCount,
                                                List<BlockCitation> citations, UUID auditId) {
        return new AiContextResolutionResult(documentId, contextText, tokenCount, blockCount, citations, auditId);
    }
}
