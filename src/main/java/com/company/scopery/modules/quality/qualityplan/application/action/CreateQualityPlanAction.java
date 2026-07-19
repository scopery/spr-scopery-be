package com.company.scopery.modules.quality.qualityplan.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.qualityplan.application.command.CreateQualityPlanCommand;
import com.company.scopery.modules.quality.qualityplan.application.response.QualityPlanResponse;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlan;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlanRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateQualityPlanAction {
    private final ProjectRepository projects;
    private final QualityPlanRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateQualityPlanAction(ProjectRepository projects, QualityPlanRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects = projects; this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public QualityPlanResponse execute(CreateQualityPlanCommand command) {
        authorization.requireQualityCreate(command.projectId());
        Project project = projects.findById(command.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(command.projectId());
        if (command.name() == null || command.name().isBlank()) throw QualityExceptions.nameRequired();
        QualityPlan saved = repo.save(QualityPlan.create(project.id(), project.workspaceId(), command.code(),
                command.name().trim(), command.description(), command.qualityObjectives(), command.testStrategy(),
                command.entryCriteria(), command.exitCriteria()));
        activityLogger.logSuccess(QualityEntityTypes.QUALITY_PLAN, saved.id(), QualityActivityActions.QUALITY_PLAN_CREATED, "Quality plan created");
        return QualityPlanResponse.from(saved);
    }
}
