package com.company.scopery.modules.quality.testcase.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testcase.application.command.CreateTestCaseCommand;
import com.company.scopery.modules.quality.testcase.application.response.BatchCreateResult;
import com.company.scopery.modules.quality.testcase.application.response.BatchCreateResult.BatchRowError;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse;
import com.company.scopery.modules.quality.testcase.domain.enums.*;
import com.company.scopery.modules.quality.testcase.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class BatchCreateTestCasesAction {
    private final ProjectRepository projects;
    private final TestCaseRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public BatchCreateTestCasesAction(ProjectRepository projects, TestCaseRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public BatchCreateResult execute(UUID projectId, List<CreateTestCaseCommand> items) {
        authorization.requireTestCreate(projectId);
        Project project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(projectId);
        List<TestCaseResponse> created = new ArrayList<>();
        List<BatchRowError> errors = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            var c = items.get(i);
            try {
                if (c.title() == null || c.title().isBlank()) throw new IllegalArgumentException("title is required");
                if (c.code() != null && repo.existsByProjectIdAndCode(projectId, c.code())) throw new IllegalArgumentException("code already exists: " + c.code());
                var type = QualityEnumParser.parseOptional(TestCaseType.class, c.type(), "type");
                if (type == null) type = TestCaseType.FUNCTIONAL;
                var priority = QualityEnumParser.parseOptional(TestCasePriority.class, c.priority(), "priority");
                if (priority == null) priority = TestCasePriority.MEDIUM;
                var automationStatus = QualityEnumParser.parseOptional(AutomationStatus.class, c.automationStatus(), "automationStatus");
                var saved = repo.save(TestCase.create(projectId, c.testSuiteId(), c.useCaseId(), c.code(), c.title().trim(), c.description(),
                        type, priority, c.preconditions(), c.expectedResult(), c.assigneeId(), automationStatus));
                activityLogger.logSuccess(QualityEntityTypes.TEST_CASE, saved.id(), QualityActivityActions.TEST_CASE_CREATED, "Test case created in batch");
                created.add(TestCaseResponse.from(saved));
            } catch (Exception ex) {
                errors.add(new BatchRowError(i, ex.getMessage()));
            }
        }
        return new BatchCreateResult(created, errors);
    }
}
