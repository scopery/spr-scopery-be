package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger; import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*; import com.company.scopery.modules.quality.shared.error.QualityExceptions; import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testrun.application.response.TestCaseResultResponse;
import com.company.scopery.modules.quality.testrun.domain.enums.TestResultStatus; import com.company.scopery.modules.quality.testrun.domain.model.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class RecordTestCaseResultAction {
    private final TestRunRepository runs; private final TestCaseResultRepository results;
    private final QualityAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser; private final QualityActivityLogger activityLogger;
    public RecordTestCaseResultAction(TestRunRepository runs, TestCaseResultRepository results, QualityAuthorizationService authorization,
                                      CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.runs=runs; this.results=results; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseResultResponse execute(UUID projectId, UUID testRunId, UUID testCaseId, String resultStatus, String actualResult) {
        authorization.requireTestExecute(projectId);
        runs.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        var actor = currentUser.resolveCurrentUser();
        var saved = results.save(TestCaseResult.create(projectId, testRunId, testCaseId,
                QualityEnumParser.parseRequired(TestResultStatus.class, resultStatus, "resultStatus"), actualResult, actor.id()));
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_RESULT, saved.id(), QualityActivityActions.TEST_CASE_RESULT_RECORDED, "Test case result recorded");
        return TestCaseResultResponse.from(saved);
    }
}
