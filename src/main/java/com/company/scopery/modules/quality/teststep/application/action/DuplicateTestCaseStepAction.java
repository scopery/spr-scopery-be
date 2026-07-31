package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.teststep.application.response.TestCaseStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DuplicateTestCaseStepAction {
    private final TestCaseStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public DuplicateTestCaseStepAction(TestCaseStepRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseStepResponse execute(UUID projectId, UUID stepId) {
        authorization.requireTestUpdate(projectId);
        var original = repo.findByIdAndProjectId(stepId, projectId)
                .orElseThrow(() -> QualityExceptions.testCaseStepNotFound(stepId));
        int nextOrder = repo.findMaxSortOrderByTestCaseId(original.testCaseId()) + 1;
        var copy = TestCaseStep.create(original.testCaseId(), original.projectId(), nextOrder,
                original.action(), original.expectedResult(), original.screenId(), original.componentId());
        var saved = repo.save(copy);
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_STEP, saved.id(), QualityActivityActions.TEST_CASE_STEP_DUPLICATED, "Test case step duplicated");
        return TestCaseStepResponse.from(saved);
    }
}
