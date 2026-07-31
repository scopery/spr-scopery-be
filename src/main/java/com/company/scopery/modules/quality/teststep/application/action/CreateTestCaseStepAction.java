package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.teststep.application.command.CreateTestCaseStepCommand;
import com.company.scopery.modules.quality.teststep.application.response.TestCaseStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateTestCaseStepAction {
    private final TestCaseRepository testCases;
    private final TestCaseStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateTestCaseStepAction(TestCaseRepository testCases, TestCaseStepRepository repo,
                                    QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.testCases=testCases; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseStepResponse execute(CreateTestCaseStepCommand c) {
        authorization.requireTestUpdate(c.projectId());
        testCases.findByIdAndProjectId(c.testCaseId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.testCaseNotFound(c.testCaseId()));
        int nextOrder = repo.findMaxSortOrderByTestCaseId(c.testCaseId()) + 1;
        var saved = repo.save(TestCaseStep.create(c.testCaseId(), c.projectId(), nextOrder, c.action(), c.expectedResult(), c.screenId(), c.componentId()));
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_STEP, saved.id(), QualityActivityActions.TEST_CASE_STEP_CREATED, "Test case step created");
        return TestCaseStepResponse.from(saved);
    }
}
