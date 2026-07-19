package com.company.scopery.modules.quality.defect.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.defect.application.command.CreateDefectCommand;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.enums.*;
import com.company.scopery.modules.quality.defect.domain.model.Defect;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDefectAction {
    private final ProjectRepository projects;
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public CreateDefectAction(ProjectRepository projects, DefectRepository repo, QualityAuthorizationService authorization,
                              CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.projects = projects; this.repo = repo; this.authorization = authorization;
        this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public DefectResponse execute(CreateDefectCommand c) {
        authorization.requireDefectCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        Defect saved = repo.save(Defect.create(project.id(), project.workspaceId(), c.code(), c.title().trim(), c.description(),
                QualityEnumParser.parseRequired(DefectCategory.class, c.category(), "category"),
                QualityEnumParser.parseRequired(DefectSeverity.class, c.severity(), "severity"),
                QualityEnumParser.parseRequired(DefectPriority.class, c.priority(), "priority"),
                actor.id(), c.reproductionSteps(), c.expectedResult(), c.actualResult(), c.sourceTestCaseResultId()));
        activityLogger.logSuccess(QualityEntityTypes.DEFECT, saved.id(), QualityActivityActions.DEFECT_CREATED, "Defect created");
        return DefectResponse.from(saved);
    }
}
