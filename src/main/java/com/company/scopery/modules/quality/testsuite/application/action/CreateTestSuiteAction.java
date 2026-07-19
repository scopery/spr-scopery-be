package com.company.scopery.modules.quality.testsuite.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.testsuite.application.command.CreateTestSuiteCommand;
import com.company.scopery.modules.quality.testsuite.application.response.TestSuiteResponse;
import com.company.scopery.modules.quality.testsuite.domain.model.TestSuite;
import com.company.scopery.modules.quality.testsuite.domain.model.TestSuiteRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTestSuiteAction {
    private final ProjectRepository projects;
    private final TestSuiteRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateTestSuiteAction(ProjectRepository projects, TestSuiteRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestSuiteResponse execute(CreateTestSuiteCommand c) {
        authorization.requireTestCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var saved = repo.save(TestSuite.create(project.id(), c.testPlanId(), c.name().trim(), c.description(), c.deliverableId(), c.scopeItemId(), c.sortOrder()));
        activityLogger.logSuccess(QualityEntityTypes.TEST_SUITE, saved.id(), QualityActivityActions.TEST_SUITE_CREATED, "TestSuite created");
        return TestSuiteResponse.from(saved);
    }
}
