package com.company.scopery.modules.scope.report.application.service;
import com.company.scopery.modules.scope.criteria.domain.enums.AcceptanceCriteriaStatus;
import com.company.scopery.modules.scope.criteria.domain.model.AcceptanceCriteriaRepository;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidenceRepository;
import com.company.scopery.modules.scope.mapping.domain.model.ScopeItemWbsMappingRepository;
import com.company.scopery.modules.scope.report.application.response.*;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap; import java.util.HashSet; import java.util.Map; import java.util.UUID;
@Service
public class ScopeReportQueryService {
    private final ScopePackageRepository packages;
    private final ScopeItemRepository items;
    private final DeliverableRepository deliverables;
    private final AcceptanceCriteriaRepository criteria;
    private final AcceptanceEvidenceRepository evidence;
    private final ScopeItemWbsMappingRepository wbsMappings;
    private final ScopeAuthorizationService authorization;
    public ScopeReportQueryService(ScopePackageRepository packages, ScopeItemRepository items,
                                   DeliverableRepository deliverables, AcceptanceCriteriaRepository criteria,
                                   AcceptanceEvidenceRepository evidence, ScopeItemWbsMappingRepository wbsMappings,
                                   ScopeAuthorizationService authorization) {
        this.packages = packages; this.items = items; this.deliverables = deliverables;
        this.criteria = criteria; this.evidence = evidence; this.wbsMappings = wbsMappings;
        this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public ScopeCoverageReportResponse scopeCoverage(UUID projectId) {
        authorization.requireScopeView(projectId);
        var projectItems = items.findByProjectId(projectId);
        long inScope = projectItems.stream().filter(i -> i.inScope() && !i.outOfScope()).count();
        long outOfScope = projectItems.stream().filter(i -> i.outOfScope()).count();
        long wbsCount = wbsMappings.countActiveByProjectId(projectId);
        return new ScopeCoverageReportResponse(packages.findByProjectId(projectId).size(), projectItems.size(),
                inScope, outOfScope, wbsCount);
    }
    @Transactional(readOnly = true)
    public DeliverableStatusReportResponse deliverableStatus(UUID projectId) {
        authorization.requireDeliverableView(projectId);
        Map<String, Long> counts = new HashMap<>();
        deliverables.findByProjectId(projectId).forEach(d ->
                counts.merge(d.status().name(), 1L, Long::sum));
        return new DeliverableStatusReportResponse(counts);
    }
    @Transactional(readOnly = true)
    public AcceptanceCriteriaReportResponse acceptanceCriteria(UUID projectId) {
        authorization.requireDeliverableView(projectId);
        Map<String, Long> counts = new HashMap<>();
        long mandatoryOpen = 0;
        for (var d : deliverables.findByProjectId(projectId)) {
            for (var c : criteria.findByDeliverableId(d.id())) {
                counts.merge(c.status().name(), 1L, Long::sum);
                if (c.mandatory() && c.status() == AcceptanceCriteriaStatus.OPEN) mandatoryOpen++;
            }
        }
        return new AcceptanceCriteriaReportResponse(counts, mandatoryOpen);
    }
    @Transactional(readOnly = true)
    public AcceptanceEvidenceReportResponse acceptanceEvidence(UUID projectId) {
        authorization.requireDeliverableView(projectId);
        long total = evidence.countByProjectId(projectId);
        var deliverableIds = new HashSet<UUID>();
        for (var d : deliverables.findByProjectId(projectId)) {
            if (!evidence.findByDeliverableId(d.id()).isEmpty()) deliverableIds.add(d.id());
        }
        return new AcceptanceEvidenceReportResponse(total, deliverableIds.size());
    }
}
