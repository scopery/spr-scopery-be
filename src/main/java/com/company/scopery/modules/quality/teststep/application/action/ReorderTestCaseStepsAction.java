package com.company.scopery.modules.quality.teststep.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.teststep.application.command.ReorderTestCaseStepsCommand;
import com.company.scopery.modules.quality.teststep.application.response.TestCaseStepResponse;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Component
public class ReorderTestCaseStepsAction {
    private final TestCaseStepRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public ReorderTestCaseStepsAction(TestCaseStepRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public List<TestCaseStepResponse> execute(ReorderTestCaseStepsCommand c) {
        authorization.requireTestUpdate(c.projectId());
        var existing = repo.findByTestCaseIdOrderBySortOrder(c.testCaseId());
        Map<UUID, TestCaseStep> byId = new HashMap<>();
        for (var s : existing) byId.put(s.id(), s);
        List<TestCaseStep> reordered = new ArrayList<>();
        for (int i = 0; i < c.orderedStepIds().size(); i++) {
            UUID stepId = c.orderedStepIds().get(i);
            var step = byId.get(stepId);
            if (step == null) throw QualityExceptions.testCaseStepNotFound(stepId);
            reordered.add(step.withSortOrder(i + 1));
        }
        repo.saveAll(reordered);
        activityLogger.logSuccess(QualityEntityTypes.TEST_CASE_STEP, c.testCaseId(), QualityActivityActions.TEST_CASE_STEPS_REORDERED, "Test case steps reordered");
        return reordered.stream().map(TestCaseStepResponse::from).toList();
    }
}
