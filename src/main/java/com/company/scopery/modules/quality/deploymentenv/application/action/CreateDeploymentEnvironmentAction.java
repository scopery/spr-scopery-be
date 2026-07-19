package com.company.scopery.modules.quality.deploymentenv.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.deploymentenv.application.command.CreateDeploymentEnvironmentCommand;
import com.company.scopery.modules.quality.deploymentenv.application.response.DeploymentEnvironmentResponse;
import com.company.scopery.modules.quality.deploymentenv.domain.enums.EnvironmentType;
import com.company.scopery.modules.quality.deploymentenv.domain.model.DeploymentEnvironment;
import com.company.scopery.modules.quality.deploymentenv.domain.model.DeploymentEnvironmentRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDeploymentEnvironmentAction {
    private final ProjectRepository projects;
    private final DeploymentEnvironmentRepository repo;
    private final QualityAuthorizationService authorization;
    public CreateDeploymentEnvironmentAction(ProjectRepository projects, DeploymentEnvironmentRepository repo, QualityAuthorizationService authorization) {
        this.projects=projects; this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public DeploymentEnvironmentResponse execute(CreateDeploymentEnvironmentCommand c) {
        authorization.requireDeploymentManage(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var type = QualityEnumParser.parseRequired(EnvironmentType.class, c.environmentType(), "environmentType");
        return DeploymentEnvironmentResponse.from(repo.save(DeploymentEnvironment.create(project.workspaceId(), project.id(), c.code().trim(), c.name().trim(), type, c.description())));
    }
}
