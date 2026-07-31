package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testrun.application.command.UpdateTestCaseResultCommand;
import com.company.scopery.modules.quality.testrun.application.response.TestCaseResultResponse;
import com.company.scopery.modules.quality.testrun.domain.enums.TestResultStatus;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResultRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateTestCaseResultAction {
    private final TestCaseResultRepository results;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public UpdateTestCaseResultAction(TestCaseResultRepository results, QualityAuthorizationService authorization,
                                      CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.results=results; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestCaseResultResponse execute(UpdateTestCaseResultCommand c) {
        authorization.requireTestExecute(c.projectId());
        var result = results.findByIdAndProjectId(c.resultId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.testResultNotFound(c.resultId()));
        if (c.version() != null && c.version() != result.version()) throw QualityExceptions.staleVersion();
        var newStatus = QualityEnumParser.parseRequired(TestResultStatus.class, c.result(), "result");
        var actor = currentUser.resolveCurrentUser();
        var updated = result.update(newStatus, c.comment(), actor.id());
        var saved = results.save(updated);
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_RESULT, saved.id(), QualityActivityActions.TEST_RUN_RESULT_UPDATED, "Test case result updated");
        return TestCaseResultResponse.from(saved);
    }
}
