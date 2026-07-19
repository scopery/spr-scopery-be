package com.company.scopery.modules.quality.qualityplan.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.qualityplan.application.response.QualityPlanResponse;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlanRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveQualityPlanAction {
    private final QualityPlanRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    public ArchiveQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser) {
        this.repo = repo; this.authorization = authorization; this.currentUser = currentUser;
    }
    @Transactional
    public QualityPlanResponse execute(UUID projectId, UUID qualityPlanId) {
        authorization.requireQualityUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var plan = repo.findByIdAndProjectId(qualityPlanId, projectId)
                .orElseThrow(() -> QualityExceptions.qualityPlanNotFound(qualityPlanId));
        return QualityPlanResponse.from(repo.save(plan.archive(actor.id())));
    }
}
