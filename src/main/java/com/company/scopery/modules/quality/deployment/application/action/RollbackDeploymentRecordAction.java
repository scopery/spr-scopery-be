package com.company.scopery.modules.quality.deployment.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.deployment.application.response.DeploymentRecordResponse;
import com.company.scopery.modules.quality.deployment.domain.enums.DeploymentStatus;
import com.company.scopery.modules.quality.deployment.domain.model.DeploymentRecordRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RollbackDeploymentRecordAction {
    private final DeploymentRecordRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public RollbackDeploymentRecordAction(DeploymentRecordRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser;
    }
    @Transactional
    public DeploymentRecordResponse execute(UUID projectId, UUID id, String reason) {
        authorization.requireDeploymentManage(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.deploymentNotFound(id));
        if (e.status() != DeploymentStatus.SUCCEEDED && e.status() != DeploymentStatus.FAILED)
            throw QualityExceptions.deploymentInvalidStatus("Can only rollback SUCCEEDED or FAILED");
        return DeploymentRecordResponse.from(repo.save(e.rollback(currentUser.resolveCurrentUser().id(), reason)));
    }
}
