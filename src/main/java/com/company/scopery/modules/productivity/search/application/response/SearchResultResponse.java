package com.company.scopery.modules.productivity.search.application.response;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record SearchResultResponse(String targetType, UUID targetId, UUID workspaceId, UUID projectId, String title, String subtitle,
        String snippet, String status, Instant updatedAt, List<String> matchedFields, double score, String navigationPath, boolean masked) {}
