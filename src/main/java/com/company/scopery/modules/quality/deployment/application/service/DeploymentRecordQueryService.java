package com.company.scopery.modules.quality.deployment.application.service;
import com.company.scopery.modules.quality.deployment.application.response.DeploymentRecordResponse;
import com.company.scopery.modules.quality.deployment.domain.model.DeploymentRecordRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DeploymentRecordQueryService {
    private final DeploymentRecordRepository repo;
    private final QualityAuthorizationService authorization;
    public DeploymentRecordQueryService(DeploymentRecordRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DeploymentRecordResponse> list(UUID projectId) {
        authorization.requireDeploymentView(projectId);
        return repo.findByProjectId(projectId).stream().map(DeploymentRecordResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public DeploymentRecordResponse get(UUID projectId, UUID id) {
        authorization.requireDeploymentView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(DeploymentRecordResponse::from)
                .orElseThrow(() -> QualityExceptions.deploymentNotFound(id));
    }
}
