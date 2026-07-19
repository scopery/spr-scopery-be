package com.company.scopery.modules.quality.deploymentenv.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.deploymentenv.application.response.DeploymentEnvironmentResponse;
import com.company.scopery.modules.quality.deploymentenv.domain.model.DeploymentEnvironmentRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveDeploymentEnvironmentAction {
    private final DeploymentEnvironmentRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveDeploymentEnvironmentAction(DeploymentEnvironmentRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public DeploymentEnvironmentResponse execute(UUID projectId, UUID id) {
        authorization.requireDeploymentManage(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.deploymentEnvNotFound(id));
        return DeploymentEnvironmentResponse.from(repo.save(e.archive(currentUser.resolveCurrentUser().id())));
    }
}
