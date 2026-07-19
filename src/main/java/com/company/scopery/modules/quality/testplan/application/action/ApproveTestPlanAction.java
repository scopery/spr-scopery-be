package com.company.scopery.modules.quality.testplan.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testplan.application.response.TestPlanResponse;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlanRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ApproveTestPlanAction {
    private final TestPlanRepository repo; private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser; private final QualityActivityLogger activityLogger;
    public ApproveTestPlanAction(TestPlanRepository repo, QualityAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public TestPlanResponse execute(UUID projectId, UUID testPlanId) {
        authorization.requireTestUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var plan = repo.findByIdAndProjectId(testPlanId, projectId).orElseThrow(() -> QualityExceptions.testPlanNotFound(testPlanId));
        var saved = repo.save(plan.approve(actor.id()));
        activityLogger.logSuccess(QualityEntityTypes.TEST_PLAN, saved.id(), QualityActivityActions.TEST_PLAN_APPROVED, "Test plan approved");
        return TestPlanResponse.from(saved);
    }
}
