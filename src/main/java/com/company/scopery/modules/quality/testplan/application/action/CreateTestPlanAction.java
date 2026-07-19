package com.company.scopery.modules.quality.testplan.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.testplan.application.command.CreateTestPlanCommand;
import com.company.scopery.modules.quality.testplan.application.response.TestPlanResponse;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlan;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlanRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTestPlanAction {
    private final ProjectRepository projects;
    private final TestPlanRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateTestPlanAction(ProjectRepository projects, TestPlanRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects = projects; this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public TestPlanResponse execute(CreateTestPlanCommand c) {
        authorization.requireTestCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var saved = repo.save(TestPlan.create(project.id(), project.workspaceId(), c.qualityPlanId(), c.releasePackageId(), c.code(),
                c.name().trim(), c.description(), QualityEnumParser.parseRequired(com.company.scopery.modules.quality.testplan.domain.enums.TestLevel.class, c.testLevel(), "testLevel")));
        activityLogger.logSuccess(QualityEntityTypes.TEST_PLAN, saved.id(), QualityActivityActions.TEST_PLAN_CREATED, "Test plan created");
        return TestPlanResponse.from(saved);
    }
}
