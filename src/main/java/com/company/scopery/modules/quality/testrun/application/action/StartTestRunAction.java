package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testrun.application.response.TestRunResponse;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import com.company.scopery.modules.quality.testrun.domain.enums.TestResultStatus;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResult;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResultRepository;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunMembershipRepository;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import com.company.scopery.modules.quality.verificationresult.domain.enums.VerificationResultStatus;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResult;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResultRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class StartTestRunAction {
    private final TestRunRepository repo;
    private final TestRunMembershipRepository membership;
    private final TestCaseResultRepository testCaseResults;
    private final VerificationCaseResultRepository verificationResults;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public StartTestRunAction(TestRunRepository repo, TestRunMembershipRepository membership,
            TestCaseResultRepository testCaseResults, VerificationCaseResultRepository verificationResults,
            QualityAuthorizationService authorization, CurrentUserAuthorizationService currentUser,
            QualityActivityLogger activityLogger) {
        this.repo=repo; this.membership=membership; this.testCaseResults=testCaseResults;
        this.verificationResults=verificationResults; this.authorization=authorization;
        this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestRunResponse execute(UUID projectId, UUID testRunId) {
        authorization.requireTestExecute(projectId);
        var actor = currentUser.resolveCurrentUser();
        var run = repo.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        try {
            var saved = repo.save(run.start(actor.id()));
            materializeMembershipResults(saved.projectId(), saved.id());
            activityLogger.logSuccess(QualityEntityTypes.TEST_RUN, saved.id(), QualityActivityActions.TEST_RUN_STARTED, "Test run started");
            return TestRunResponse.from(saved);
        } catch (IllegalStateException ex) { throw QualityExceptions.testRunInvalidStatus(ex.getMessage()); }
    }

    private void materializeMembershipResults(UUID projectId, UUID testRunId) {
        for (var item : membership.findByTestRunId(testRunId)) {
            if (item.caseKind() == MembershipCaseKind.FUNCTIONAL) {
                testCaseResults.save(TestCaseResult.create(projectId, testRunId, item.caseId(), TestResultStatus.NOT_RUN, null, null));
            } else {
                verificationResults.save(VerificationCaseResult.create(projectId, testRunId, item.caseId(),
                        VerificationResultStatus.NOT_RUN, null, null, null, null, null, null, null));
            }
        }
    }
}
