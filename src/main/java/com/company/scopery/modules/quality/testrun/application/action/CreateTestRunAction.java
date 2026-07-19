package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger; import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*; import com.company.scopery.modules.quality.shared.error.QualityExceptions; import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testrun.application.command.CreateTestRunCommand; import com.company.scopery.modules.quality.testrun.application.response.TestRunResponse;
import com.company.scopery.modules.quality.testrun.domain.enums.TestRunType; import com.company.scopery.modules.quality.testrun.domain.model.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTestRunAction {
    private final ProjectRepository projects; private final TestRunRepository repo;
    private final QualityAuthorizationService authorization; private final QualityActivityLogger activityLogger;
    public CreateTestRunAction(ProjectRepository projects, TestRunRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestRunResponse execute(CreateTestRunCommand c) {
        authorization.requireTestCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var saved = repo.save(TestRun.create(project.id(), project.workspaceId(), c.name().trim(),
                QualityEnumParser.parseRequired(TestRunType.class, c.runType(), "runType"), c.testPlanId(), c.testSuiteId(), c.releasePackageId()));
        activityLogger.logSuccess(QualityEntityTypes.TEST_RUN, saved.id(), QualityActivityActions.TEST_RUN_CREATED, "Test run created");
        return TestRunResponse.from(saved);
    }
}
