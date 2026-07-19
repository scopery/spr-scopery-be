package com.company.scopery.modules.quality.rollbackplan.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.rollbackplan.application.response.RollbackPlanResponse;
import com.company.scopery.modules.quality.rollbackplan.domain.model.RollbackPlanRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ApproveRollbackPlanAction {
    private final RollbackPlanRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public ApproveRollbackPlanAction(RollbackPlanRepository repo, QualityAuthorizationService authorization,
                                 CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public RollbackPlanResponse execute(UUID projectId, UUID id) {
        authorization.requireDeploymentManage(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> QualityExceptions.rollbackPlanNotFound(id));
        var saved = repo.save(e.approve(currentUser.resolveCurrentUser().id()));
        activityLogger.logSuccess(QualityEntityTypes.ROLLBACK_PLAN, saved.id(), QualityActivityActions.ROLLBACK_PLAN_APPROVED, "Rollback plan approved");
        return RollbackPlanResponse.from(saved);
    }
}
