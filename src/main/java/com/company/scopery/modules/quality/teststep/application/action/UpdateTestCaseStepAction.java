package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.teststep.application.command.UpdateTestCaseStepCommand;
import com.company.scopery.modules.quality.teststep.application.response.TestCaseStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateTestCaseStepAction {
    private final TestCaseStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public UpdateTestCaseStepAction(TestCaseStepRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseStepResponse execute(UpdateTestCaseStepCommand c) {
        authorization.requireTestUpdate(c.projectId());
        var step = repo.findByIdAndProjectId(c.stepId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.testCaseStepNotFound(c.stepId()));
        if (c.version() != null && c.version() != step.version()) throw QualityExceptions.staleVersion();
        var updated = step.update(c.action(), c.expectedResult(), c.screenId(), c.componentId());
        var saved = repo.save(updated);
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_STEP, saved.id(), QualityActivityActions.TEST_CASE_STEP_UPDATED, "Test case step updated");
        return TestCaseStepResponse.from(saved);
    }
}
