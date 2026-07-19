package com.company.scopery.modules.quality.rollbackplan.application.service;
import com.company.scopery.modules.quality.rollbackplan.application.response.RollbackPlanResponse;
import com.company.scopery.modules.quality.rollbackplan.domain.model.RollbackPlanRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class RollbackPlanQueryService {
    private final RollbackPlanRepository repo;
    private final QualityAuthorizationService authorization;
    public RollbackPlanQueryService(RollbackPlanRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<RollbackPlanResponse> list(UUID projectId) {
        authorization.requireDeploymentView(projectId);
        return repo.findByProjectId(projectId).stream().map(RollbackPlanResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public RollbackPlanResponse get(UUID projectId, UUID id) {
        authorization.requireDeploymentView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(RollbackPlanResponse::from)
                .orElseThrow(() -> QualityExceptions.rollbackPlanNotFound(id));
    }
}
