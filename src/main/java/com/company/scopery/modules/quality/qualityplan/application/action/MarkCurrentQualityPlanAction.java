package com.company.scopery.modules.quality.qualityplan.application.action;
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
public class MarkCurrentQualityPlanAction {
    private final QualityPlanRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public MarkCurrentQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization,
                                        QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public QualityPlanResponse execute(UUID projectId, UUID qualityPlanId) {
        authorization.requireQualityApprove(projectId);
        var plan = repo.findByIdAndProjectId(qualityPlanId, projectId)
                .orElseThrow(() -> QualityExceptions.qualityPlanNotFound(qualityPlanId));
        for (var other : repo.findByProjectId(projectId)) {
            if (other.currentFlag() && !other.id().equals(qualityPlanId)) {
                repo.save(other.clearCurrent());
            }
        }
        var saved = repo.save(plan.markCurrent());
        activityLogger.logSuccess(QualityEntityTypes.QUALITY_PLAN, saved.id(), QualityActivityActions.QUALITY_PLAN_MARKED_CURRENT, "Quality plan marked current");
        return QualityPlanResponse.from(saved);
    }
}
