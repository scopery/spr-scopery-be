package com.company.scopery.modules.quality.qualityplan.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.qualityplan.application.response.QualityPlanResponse;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlanRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ApproveQualityPlanAction {
    private final QualityPlanRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public ApproveQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public QualityPlanResponse execute(UUID projectId, UUID qualityPlanId) {
        authorization.requireQualityApprove(projectId);
        var actor = currentUser.resolveCurrentUser();
        var plan = repo.findByIdAndProjectId(qualityPlanId, projectId)
                .orElseThrow(() -> QualityExceptions.qualityPlanNotFound(qualityPlanId));
        try {
            var saved = repo.save(plan.approve(actor.id()));
            activityLogger.logSuccess(QualityEntityTypes.QUALITY_PLAN, saved.id(), QualityActivityActions.QUALITY_PLAN_APPROVED, "Quality plan approved");
            return QualityPlanResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw QualityExceptions.qualityPlanImmutable(qualityPlanId);
        }
    }
}
