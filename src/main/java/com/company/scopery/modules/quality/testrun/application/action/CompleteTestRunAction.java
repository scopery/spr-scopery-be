package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger; import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*; import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testrun.application.response.TestRunResponse; import com.company.scopery.modules.quality.testrun.domain.model.*;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CompleteTestRunAction {
    private final TestRunRepository repo; private final TestCaseResultRepository results;
    private final QualityAuthorizationService authorization; private final QualityActivityLogger activityLogger;
    public CompleteTestRunAction(TestRunRepository repo, TestCaseResultRepository results, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.results=results; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestRunResponse execute(UUID projectId, UUID testRunId) {
        authorization.requireTestExecute(projectId);
        var run = repo.findByIdAndProjectId(testRunId, projectId).orElseThrow(() -> QualityExceptions.testRunNotFound(testRunId));
        var list = results.findByTestRunId(testRunId);
        long passed = list.stream().filter(r -> r.resultStatus().name().equals("PASSED")).count();
        long failed = list.stream().filter(r -> r.resultStatus().name().equals("FAILED")).count();
        String summary = "{\"passed\":"+passed+",\"failed\":"+failed+",\"total\":"+list.size()+"}";
        try {
            var saved = repo.save(run.complete(summary));
            activityLogger.logSuccess(QualityEntityTypes.TEST_RUN, saved.id(), QualityActivityActions.TEST_RUN_COMPLETED, "Test run completed");
            return TestRunResponse.from(saved);
        } catch (IllegalStateException ex) { throw QualityExceptions.testRunInvalidStatus(ex.getMessage()); }
    }
}
