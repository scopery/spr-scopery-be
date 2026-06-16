package com.company.scopery.modules.knowledge.documenttype.application.query;

import java.util.UUID;

public record SearchDocumentTypeQuery(
        String keyword,
        UUID workspaceId,
        String documentScope,
        String status,
        boolean includeDeleted,
        int page,
        int size) {}
