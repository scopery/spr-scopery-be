package com.company.scopery.modules.quality.testcase.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testcase.application.command.CreateTestCaseCommand;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse;
import com.company.scopery.modules.quality.testcase.domain.enums.AutomationStatus;
import com.company.scopery.modules.quality.testcase.domain.enums.TestCasePriority;
import com.company.scopery.modules.quality.testcase.domain.enums.TestCaseType;
import com.company.scopery.modules.quality.testcase.domain.model.TestCase;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateTestCaseAction {
    private final ProjectRepository projects;
    private final TestCaseRepository repo;
    private final TestCaseStepRepository stepRepo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;

    public CreateTestCaseAction(
            ProjectRepository projects,
            TestCaseRepository repo,
            TestCaseStepRepository stepRepo,
            QualityAuthorizationService authorization,
            QualityActivityLogger activityLogger) {
        this.projects = projects;
        this.repo = repo;
        this.stepRepo = stepRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public TestCaseResponse execute(CreateTestCaseCommand c) {
        authorization.requireTestCreate(c.projectId());
        Project project = projects.findById(c.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw QualityExceptions.projectArchived(c.projectId());
        }
        if (c.code() != null && repo.existsByProjectIdAndCode(c.projectId(), c.code())) {
            throw QualityExceptions.testCaseCodeExists(c.code());
        }
        var type = QualityEnumParser.parseOptional(TestCaseType.class, c.type(), "type");
        if (type == null) type = TestCaseType.FUNCTIONAL;
        var priority = QualityEnumParser.parseOptional(TestCasePriority.class, c.priority(), "priority");
        if (priority == null) priority = TestCasePriority.MEDIUM;
        var automationStatus = QualityEnumParser.parseOptional(
                AutomationStatus.class, c.automationStatus(), "automationStatus");
        var saved = repo.save(TestCase.create(
                project.id(),
                c.testSuiteId(),
                c.useCaseId(),
                c.code(),
                c.title().trim(),
                c.description(),
                type,
                priority,
                c.preconditions(),
                c.expectedResult(),
                c.assigneeId(),
                automationStatus));
        activityLogger.logSuccess(
                QualityEntityTypes.TEST_CASE,
                saved.id(),
                QualityActivityActions.TEST_CASE_CREATED,
                "Test case created");

        // Nested steps are part of TEST_CREATE (same TX). Do not call BatchCreate… (requires TEST_UPDATE).
        if (c.steps() != null && !c.steps().isEmpty()) {
            for (int i = 0; i < c.steps().size(); i++) {
                var s = c.steps().get(i);
                if (s.action() == null || s.action().isBlank()) {
                    throw new IllegalArgumentException("steps[" + i + "]: action is required");
                }
                stepRepo.save(TestCaseStep.create(
                        saved.id(),
                        c.projectId(),
                        i + 1,
                        s.action().trim(),
                        s.expectedResult(),
                        s.screenId(),
                        s.componentId()));
            }
            activityLogger.logSuccess(
                    QualityEntityTypes.TEST_CASE_STEP,
                    saved.id(),
                    QualityActivityActions.TEST_CASE_STEP_BATCH_CREATED,
                    "Created " + c.steps().size() + " steps with test case");
        }

        return TestCaseResponse.from(saved);
    }
}
