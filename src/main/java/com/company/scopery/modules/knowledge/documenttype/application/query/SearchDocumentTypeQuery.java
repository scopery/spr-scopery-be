package com.company.scopery.modules.knowledge.documenttype.application.query;

import java.util.UUID;

public record SearchDocumentTypeQuery(
        String keyword,
        UUID organizationId,
        UUID workspaceId,
        String documentScope,
        String status,
        Boolean builtIn,
        boolean includeArchived,
        int page,
        int size) {

    /** Backward-compatible constructor. */
    public SearchDocumentTypeQuery(String keyword, UUID workspaceId, String documentScope, String status,
                                   boolean includeDeleted, int page, int size) {
        this(keyword, null, workspaceId, documentScope, status, null, includeDeleted, page, size);
    }
}
