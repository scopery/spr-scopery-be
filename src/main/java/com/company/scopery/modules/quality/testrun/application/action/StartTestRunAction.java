package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger; import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testrun.application.response.TestRunResponse; import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class StartTestRunAction {
    private final TestRunRepository repo; private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final QualityActivityLogger activityLogger;
    public StartTestRunAction(TestRunRepository repo, QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestRunResponse execute(UUID projectId, UUID testRunId) {
        authorization.requireTestExecute(projectId);
        var actor = currentUser.resolveCurrentUser();
        var run = repo.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        try {
            var saved = repo.save(run.start(actor.id()));
            activityLogger.logSuccess(QualityEntityTypes.TEST_RUN, saved.id(), QualityActivityActions.TEST_RUN_STARTED, "Test run started");
            return TestRunResponse.from(saved);
        } catch (IllegalStateException ex) { throw QualityExceptions.testRunInvalidStatus(ex.getMessage()); }
    }
}
