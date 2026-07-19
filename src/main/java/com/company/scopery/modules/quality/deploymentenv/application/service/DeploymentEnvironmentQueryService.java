package com.company.scopery.modules.quality.deploymentenv.application.service;
import com.company.scopery.modules.quality.deploymentenv.application.response.DeploymentEnvironmentResponse;
import com.company.scopery.modules.quality.deploymentenv.domain.model.DeploymentEnvironmentRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Service
public class DeploymentEnvironmentQueryService {
    private final DeploymentEnvironmentRepository repo;
    private final QualityAuthorizationService authorization;
    public DeploymentEnvironmentQueryService(DeploymentEnvironmentRepository repo, QualityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional(readOnly=true)
    public List<DeploymentEnvironmentResponse> list(UUID projectId) {
        authorization.requireDeploymentView(projectId);
        return repo.findByProjectId(projectId).stream().map(DeploymentEnvironmentResponse::from).toList();
    }
    @Transactional(readOnly=true)
    public DeploymentEnvironmentResponse get(UUID projectId, UUID id) {
        authorization.requireDeploymentView(projectId);
        return repo.findByIdAndProjectId(id, projectId).map(DeploymentEnvironmentResponse::from)
                .orElseThrow(() -> QualityExceptions.deploymentEnvNotFound(id));
    }
}
