package com.company.scopery.modules.traceability.report.application.service;
import com.company.scopery.modules.traceability.report.application.response.TraceCoverageMatrixResponse;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkStatus;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class TraceabilityReportService {
    private final TraceLinkRepository links;
    private final TraceabilityAuthorizationService authorization;
    public TraceabilityReportService(TraceLinkRepository links, TraceabilityAuthorizationService authorization) {
        this.links=links; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public TraceCoverageMatrixResponse coverageMatrix(UUID projectId) {
        authorization.requireView(projectId);
        List<TraceLink> all = links.findByProjectId(projectId);
        long active = all.stream().filter(l -> l.status() == TraceLinkStatus.ACTIVE).count();
        Map<String, Long> byType = all.stream().filter(l -> l.status() == TraceLinkStatus.ACTIVE)
                .collect(Collectors.groupingBy(l -> l.linkType().name(), Collectors.counting()));
        List<Map<String, Object>> byLinkType = byType.entrySet().stream()
                .map(e -> Map.<String, Object>of("linkType", e.getKey(), "count", e.getValue()))
                .toList();
        // Gap heuristic: sources with no outbound links of type COVERS/SATISFIES are not computed without full catalog;
        // report orphans where target has no inbound COVERS from REQUIREMENT.
        Set<String> coveredTargets = all.stream()
                .filter(l -> l.status() == TraceLinkStatus.ACTIVE)
                .map(l -> l.targetType() + ":" + l.targetId())
                .collect(Collectors.toSet());
        List<Map<String, Object>> gaps = all.stream()
                .filter(l -> l.status() == TraceLinkStatus.ACTIVE)
                .filter(l -> "REQUIREMENT".equalsIgnoreCase(l.sourceType()))
                .filter(l -> !coveredTargets.contains(l.sourceType() + ":" + l.sourceId()))
                .map(l -> Map.<String, Object>of("sourceType", l.sourceType(), "sourceId", l.sourceId().toString(), "gap", "NO_INBOUND_COVER"))
                .distinct()
                .limit(100)
                .toList();
        return new TraceCoverageMatrixResponse(projectId, all.size(), active, byLinkType, gaps);
    }
}
