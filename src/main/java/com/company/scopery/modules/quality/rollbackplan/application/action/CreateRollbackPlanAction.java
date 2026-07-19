package com.company.scopery.modules.quality.rollbackplan.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.rollbackplan.application.command.CreateRollbackPlanCommand;
import com.company.scopery.modules.quality.rollbackplan.application.response.RollbackPlanResponse;
import com.company.scopery.modules.quality.rollbackplan.domain.model.RollbackPlan;
import com.company.scopery.modules.quality.rollbackplan.domain.model.RollbackPlanRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRollbackPlanAction {
    private final ProjectRepository projects;
    private final RollbackPlanRepository repo;
    private final QualityAuthorizationService authorization;
    public CreateRollbackPlanAction(ProjectRepository projects, RollbackPlanRepository repo, QualityAuthorizationService authorization) {
        this.projects=projects; this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RollbackPlanResponse execute(CreateRollbackPlanCommand c) {
        authorization.requireDeploymentManage(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        return RollbackPlanResponse.from(repo.save(RollbackPlan.create(project.id(), c.releasePackageId(), c.deploymentRecordId(), c.title().trim(), c.description().trim(), c.ownerUserId(), c.stepsJson())));
    }
}
