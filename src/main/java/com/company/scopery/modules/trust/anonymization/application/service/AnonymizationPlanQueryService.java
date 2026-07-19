package com.company.scopery.modules.trust.anonymization.application.service;
import com.company.scopery.modules.trust.anonymization.application.response.AnonymizationPlanResponse;
import com.company.scopery.modules.trust.anonymization.domain.model.AnonymizationPlanRepository;
import com.company.scopery.modules.trust.shared.authorization.TrustAuthorizationService;
import com.company.scopery.modules.trust.shared.error.TrustExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class AnonymizationPlanQueryService {
    private final AnonymizationPlanRepository repo;
    private final TrustAuthorizationService auth;
    public AnonymizationPlanQueryService(AnonymizationPlanRepository repo, TrustAuthorizationService auth) {
        this.repo = repo; this.auth = auth;
    }
    @Transactional(readOnly = true)
    public List<AnonymizationPlanResponse> listByWorkspace(UUID workspaceId) {
        auth.requireView(workspaceId);
        return repo.findByWorkspaceId(workspaceId).stream().map(AnonymizationPlanResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public AnonymizationPlanResponse getById(UUID workspaceId, UUID planId) {
        auth.requireView(workspaceId);
        return repo.findById(planId).map(AnonymizationPlanResponse::from)
                .orElseThrow(() -> TrustExceptions.evidenceNotFound(planId));
    }
}
