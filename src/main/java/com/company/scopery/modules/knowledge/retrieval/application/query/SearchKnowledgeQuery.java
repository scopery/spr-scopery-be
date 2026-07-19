package com.company.scopery.modules.knowledge.retrieval.application.query;

import java.util.List;
import java.util.UUID;

public record SearchKnowledgeQuery(
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        List<String> aclTokens,
        String query,
        Integer topK
) {}
