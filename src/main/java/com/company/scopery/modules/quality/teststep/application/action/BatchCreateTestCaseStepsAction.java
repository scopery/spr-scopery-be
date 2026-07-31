package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.teststep.application.command.BatchCreateTestCaseStepsCommand;
import com.company.scopery.modules.quality.teststep.application.response.BatchCreateStepsResult;
import com.company.scopery.modules.quality.teststep.application.response.BatchCreateStepsResult.BatchStepError;
import com.company.scopery.modules.quality.teststep.application.response.TestCaseStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class BatchCreateTestCaseStepsAction {
    private final TestCaseRepository testCases;
    private final TestCaseStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public BatchCreateTestCaseStepsAction(TestCaseRepository testCases, TestCaseStepRepository repo,
                                          QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.testCases=testCases; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public BatchCreateStepsResult execute(BatchCreateTestCaseStepsCommand c) {
        authorization.requireTestUpdate(c.projectId());
        testCases.findByIdAndProjectId(c.testCaseId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.testCaseNotFound(c.testCaseId()));
        int baseOrder = repo.findMaxSortOrderByTestCaseId(c.testCaseId());
        List<TestCaseStepResponse> created = new ArrayList<>();
        List<BatchStepError> errors = new ArrayList<>();
        for (int i = 0; i < c.items().size(); i++) {
            var item = c.items().get(i);
            try {
                if (item.action() == null || item.action().isBlank()) throw new IllegalArgumentException("action is required");
                var saved = repo.save(TestCaseStep.create(c.testCaseId(), c.projectId(), baseOrder + i + 1,
                        item.action().trim(), item.expectedResult(), item.screenId(), item.componentId()));
                created.add(TestCaseStepResponse.from(saved));
            } catch (Exception ex) {
                errors.add(new BatchStepError(i, ex.getMessage()));
            }
        }
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_STEP, c.testCaseId(), QualityActivityActions.TEST_CASE_STEP_BATCH_CREATED, "Batch created " + created.size() + " steps");
        return new BatchCreateStepsResult(created, errors);
    }
}
