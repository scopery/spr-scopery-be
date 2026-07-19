package com.company.scopery.modules.traceability.report.application.response;
import java.util.List; import java.util.Map; import java.util.UUID;
public record TraceCoverageMatrixResponse(UUID projectId, long totalLinks, long activeLinks, List<Map<String, Object>> byLinkType, List<Map<String, Object>> gaps) {}
