package com.company.scopery.modules.quality.testcase.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testcase.application.command.BatchUpdateTestCasesCommand;
import com.company.scopery.modules.quality.testcase.application.response.BatchUpdateResult;
import com.company.scopery.modules.quality.testcase.application.response.BatchUpdateResult.BatchFailure;
import com.company.scopery.modules.quality.testcase.domain.enums.*;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class BatchUpdateTestCasesAction {
    private final TestCaseRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public BatchUpdateTestCasesAction(TestCaseRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public BatchUpdateResult execute(BatchUpdateTestCasesCommand c) {
        authorization.requireTestUpdate(c.projectId());
        List<UUID> updated = new ArrayList<>();
        List<BatchFailure> failed = new ArrayList<>();
        var priority = QualityEnumParser.parseOptional(TestCasePriority.class, c.priority(), "priority");
        var status = QualityEnumParser.parseOptional(TestCaseStatus.class, c.status(), "status");
        var automationStatus = QualityEnumParser.parseOptional(AutomationStatus.class, c.automationStatus(), "automationStatus");
        for (UUID id : c.testCaseIds()) {
            try {
                var tc = repo.findByIdAndProjectId(id, c.projectId()).orElseThrow(() -> new IllegalArgumentException("not found"));
                var u = tc.update(tc.title(), tc.description(), tc.type(),
                        priority != null ? priority : tc.priority(),
                        status != null ? status : tc.status(),
                        tc.preconditions(), tc.expectedResult(),
                        tc.useCaseId(),
                        c.assigneeId() != null ? c.assigneeId() : tc.assigneeId(),
                        automationStatus != null ? automationStatus : tc.automationStatus());
                repo.save(u);
                updated.add(id);
            } catch (Exception ex) {
                failed.add(new BatchFailure(id, ex.getMessage()));
            }
        }
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE, null, QualityActivityActions.TEST_CASE_BATCH_UPDATED, "Batch updated " + updated.size() + " test cases");
        return new BatchUpdateResult(updated, failed);
    }
}
