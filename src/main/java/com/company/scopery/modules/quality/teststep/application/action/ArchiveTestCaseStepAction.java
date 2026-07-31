package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.teststep.application.response.TestCaseStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveTestCaseStepAction {
    private final TestCaseStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public ArchiveTestCaseStepAction(TestCaseStepRepository repo, QualityAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseStepResponse execute(UUID projectId, UUID stepId) {
        authorization.requireTestUpdate(projectId);
        var step = repo.findByIdAndProjectId(stepId, projectId)
                .orElseThrow(() -> QualityExceptions.testCaseStepNotFound(stepId));
        var actor = currentUser.resolveCurrentUser();
        var saved = repo.save(step.archive(actor.id()));
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_STEP, saved.id(), QualityActivityActions.TEST_CASE_STEP_ARCHIVED, "Test case step archived");
        return TestCaseStepResponse.from(saved);
    }
}
