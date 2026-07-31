package com.company.scopery.modules.quality.testcase.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger; import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*; import com.company.scopery.modules.quality.shared.error.QualityExceptions; import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testcase.application.command.CreateTestCaseCommand; import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse;
import com.company.scopery.modules.quality.testcase.domain.enums.*; import com.company.scopery.modules.quality.testcase.domain.model.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTestCaseAction {
    private final ProjectRepository projects; private final TestCaseRepository repo;
    private final QualityAuthorizationService authorization; private final QualityActivityLogger activityLogger;
    public CreateTestCaseAction(ProjectRepository projects, TestCaseRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseResponse execute(CreateTestCaseCommand c) {
        authorization.requireTestCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        if (c.code() != null && repo.existsByProjectIdAndCode(c.projectId(), c.code())) throw QualityExceptions.testCaseCodeExists(c.code());
        var type = QualityEnumParser.parseOptional(TestCaseType.class, c.type(), "type");
        if (type == null) type = TestCaseType.FUNCTIONAL;
        var priority = QualityEnumParser.parseOptional(TestCasePriority.class, c.priority(), "priority");
        if (priority == null) priority = TestCasePriority.MEDIUM;
        var automationStatus = QualityEnumParser.parseOptional(AutomationStatus.class, c.automationStatus(), "automationStatus");
        var saved = repo.save(TestCase.create(project.id(), c.testSuiteId(), c.useCaseId(), c.code(), c.title().trim(), c.description(),
                type, priority, c.preconditions(), c.expectedResult(), c.assigneeId(), automationStatus));
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE, saved.id(), QualityActivityActions.TEST_CASE_CREATED, "Test case created");
        return TestCaseResponse.from(saved);
    }
}
