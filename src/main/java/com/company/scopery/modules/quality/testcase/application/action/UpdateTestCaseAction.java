package com.company.scopery.modules.quality.testcase.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testcase.application.command.UpdateTestCaseCommand;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse;
import com.company.scopery.modules.quality.testcase.domain.enums.*;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateTestCaseAction {
    private final TestCaseRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public UpdateTestCaseAction(TestCaseRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseResponse execute(UpdateTestCaseCommand c) {
        authorization.requireTestUpdate(c.projectId());
        var tc = repo.findByIdAndProjectId(c.testCaseId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.testCaseNotFound(c.testCaseId()));
        if (c.version() != null && c.version() != tc.version()) throw QualityExceptions.staleVersion();
        var type = c.type() != null ? QualityEnumParser.parseRequired(TestCaseType.class, c.type(), "type") : tc.type();
        var priority = c.priority() != null ? QualityEnumParser.parseRequired(TestCasePriority.class, c.priority(), "priority") : tc.priority();
        var status = c.status() != null ? QualityEnumParser.parseRequired(TestCaseStatus.class, c.status(), "status") : tc.status();
        var automationStatus = c.automationStatus() != null ? QualityEnumParser.parseRequired(AutomationStatus.class, c.automationStatus(), "automationStatus") : tc.automationStatus();

        String nextCode = tc.code();
        if (c.code() != null) {
            nextCode = c.code().isBlank() ? null : c.code().trim();
            if (nextCode != null
                    && !nextCode.equals(tc.code())
                    && repo.existsByProjectIdAndCode(c.projectId(), nextCode)) {
                throw QualityExceptions.testCaseCodeExists(nextCode);
            }
        }

        var updated = tc.update(
                nextCode,
                c.title() != null ? c.title().trim() : tc.title(),
                c.description() != null ? c.description() : tc.description(),
                type, priority, status,
                c.preconditions() != null ? c.preconditions() : tc.preconditions(),
                c.expectedResult() != null ? c.expectedResult() : tc.expectedResult(),
                c.useCaseId(),
                c.assigneeId() != null ? c.assigneeId() : tc.assigneeId(),
                automationStatus);
        var saved = repo.save(updated);
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE, saved.id(), QualityActivityActions.TEST_CASE_UPDATED, "Test case updated");
        return TestCaseResponse.from(saved);
    }
}
