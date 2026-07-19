package com.company.scopery.modules.aiagent.tool.application.port;

import java.util.List;
import java.util.UUID;

public record AiToolResultItem(
        UUID chunkId,
        UUID sourceId,
        String sourceType,
        UUID sourceRefId,
        UUID sourceVersionRefId,
        String title,
        List<String> headingPath,
        String safeSnippet,
        double rrfScore,
        int rank,
        UUID retrievalTraceId
) {}
