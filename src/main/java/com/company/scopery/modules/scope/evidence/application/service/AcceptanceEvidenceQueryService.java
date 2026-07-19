package com.company.scopery.modules.scope.evidence.application.service;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.evidence.application.response.AcceptanceEvidenceResponse;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidenceRepository;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AcceptanceEvidenceQueryService {
    private final DeliverableRepository deliverables;
    private final AcceptanceEvidenceRepository evidence;
    private final ScopeAuthorizationService authorization;
    public AcceptanceEvidenceQueryService(DeliverableRepository deliverables, AcceptanceEvidenceRepository evidence,
                                          ScopeAuthorizationService authorization) {
        this.deliverables = deliverables; this.evidence = evidence; this.authorization = authorization;
    }
    @Transactional(readOnly = true)
    public List<AcceptanceEvidenceResponse> listByDeliverable(UUID projectId, UUID deliverableId) {
        authorization.requireDeliverableView(projectId);
        deliverables.findByIdAndProjectId(deliverableId, projectId)
                .orElseThrow(() -> ScopeExceptions.deliverableNotFound(deliverableId));
        return evidence.findByDeliverableId(deliverableId).stream().map(AcceptanceEvidenceResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public AcceptanceEvidenceResponse get(UUID projectId, UUID evidenceId) {
        authorization.requireDeliverableView(projectId);
        return evidence.findByIdAndProjectId(evidenceId, projectId).map(AcceptanceEvidenceResponse::from)
                .orElseThrow(() -> ScopeExceptions.evidenceNotFound(evidenceId));
    }
}
