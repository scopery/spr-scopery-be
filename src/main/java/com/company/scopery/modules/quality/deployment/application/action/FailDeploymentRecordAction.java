package com.company.scopery.modules.quality.deployment.application.action;
import com.company.scopery.modules.quality.deployment.application.response.DeploymentRecordResponse;
import com.company.scopery.modules.quality.deployment.domain.enums.DeploymentStatus;
import com.company.scopery.modules.quality.deployment.domain.model.DeploymentRecordRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class FailDeploymentRecordAction {
    private final DeploymentRecordRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public FailDeploymentRecordAction(DeploymentRecordRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public DeploymentRecordResponse execute(UUID projectId, UUID id, String reason) {
        authorization.requireDeploymentManage(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.deploymentNotFound(id));
        if (e.status() != DeploymentStatus.IN_PROGRESS) throw QualityExceptions.deploymentInvalidStatus("Can only fail IN_PROGRESS");
        var saved = repo.save(e.fail(reason));
        activityLogger.logSuccess(QualityEntityTypes.DEPLOYMENT_RECORD, saved.id(), QualityActivityActions.DEPLOYMENT_FAILED, "Deployment failed");
        return DeploymentRecordResponse.from(saved);
    }
}
