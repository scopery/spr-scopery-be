package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testrun.application.response.BatchUpdateResultsResponse;
import com.company.scopery.modules.quality.testrun.application.response.BatchUpdateResultsResponse.ResultFailure;
import com.company.scopery.modules.quality.testrun.domain.enums.TestResultStatus;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResult;
import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResultRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class BatchUpdateResultsAction {
    private final TestCaseResultRepository results;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public BatchUpdateResultsAction(TestCaseResultRepository results, QualityAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.results=results; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public BatchUpdateResultsResponse execute(UUID projectId, List<UUID> resultIds, String resultStr, UUID assigneeId) {
        authorization.requireTestExecute(projectId);
        var newStatus = resultStr != null ? QualityEnumParser.parseOptional(TestResultStatus.class, resultStr, "result") : null;
        var actor = currentUser.resolveCurrentUser();
        List<UUID> updated = new ArrayList<>();
        List<ResultFailure> failed = new ArrayList<>();
        for (UUID id : resultIds) {
            try {
                var result = results.findByIdAndProjectId(id, projectId).orElseThrow(() -> new IllegalArgumentException("not found"));
                var u = newStatus != null ? result.update(newStatus, result.comment(), actor.id()) : result;
                results.save(u);
                updated.add(id);
            } catch (Exception ex) {
                failed.add(new ResultFailure(id, ex.getMessage()));
            }
        }
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_RESULT, null, QualityActivityActions.TEST_RUN_RESULTS_BATCH_UPDATED, "Batch updated " + updated.size() + " results");
        return new BatchUpdateResultsResponse(updated, failed);
    }
}
