package com.company.scopery.modules.quality.testcase.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger; import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.application.response.TestCaseResponse; import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ApproveTestCaseAction {
    private final TestCaseRepository repo; private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final QualityActivityLogger activityLogger;
    public ApproveTestCaseAction(TestCaseRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseResponse execute(UUID projectId, UUID testCaseId) {
        authorization.requireTestUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var tc = repo.findByIdAndProjectId(testCaseId, projectId).orElseThrow(() -> QualityExceptions.testCaseNotFound(testCaseId));
        try {
            var saved = repo.save(tc.approve(actor.id()));
            activityLogger.logSuccess(QualityEntityTypes.TEST_CASE, saved.id(), QualityActivityActions.TEST_CASE_APPROVED, "Test case approved");
            return TestCaseResponse.from(saved);
        } catch (IllegalStateException ex) { throw QualityExceptions.testCaseImmutable(testCaseId); }
    }
}
