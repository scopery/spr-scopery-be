package com.company.scopery.modules.quality.deployment.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.deployment.application.command.CreateDeploymentRecordCommand;
import com.company.scopery.modules.quality.deployment.application.response.DeploymentRecordResponse;
import com.company.scopery.modules.quality.deployment.domain.model.DeploymentRecord;
import com.company.scopery.modules.quality.deployment.domain.model.DeploymentRecordRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDeploymentRecordAction {
    private final ProjectRepository projects;
    private final DeploymentRecordRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateDeploymentRecordAction(ProjectRepository projects, DeploymentRecordRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public DeploymentRecordResponse execute(CreateDeploymentRecordCommand c) {
        authorization.requireDeploymentManage(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var saved = repo.save(DeploymentRecord.create(project.id(), project.workspaceId(), c.releasePackageId(), c.deploymentEnvironmentId(),
                c.buildReference(), c.deploymentReference(), c.rollbackPlanId()));
        activityLogger.logSuccess(QualityEntityTypes.DEPLOYMENT_RECORD, saved.id(), QualityActivityActions.DEPLOYMENT_RECORD_CREATED, "Deployment created");
        return DeploymentRecordResponse.from(saved);
    }
}
