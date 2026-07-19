package com.company.scopery.modules.quality.qualityplan.application.action;
import com.company.scopery.modules.quality.qualityplan.application.command.UpdateQualityPlanCommand;
import com.company.scopery.modules.quality.qualityplan.application.response.QualityPlanResponse;
import com.company.scopery.modules.quality.qualityplan.domain.model.QualityPlanRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateQualityPlanAction {
    private final QualityPlanRepository repo;
    private final QualityAuthorizationService authorization;
    public UpdateQualityPlanAction(QualityPlanRepository repo, QualityAuthorizationService authorization) {
        this.repo = repo; this.authorization = authorization;
    }
    @Transactional
    public QualityPlanResponse execute(UpdateQualityPlanCommand c) {
        authorization.requireQualityUpdate(c.projectId());
        var plan = repo.findByIdAndProjectId(c.qualityPlanId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.qualityPlanNotFound(c.qualityPlanId()));
        try {
            return QualityPlanResponse.from(repo.save(plan.update(c.name().trim(), c.description(),
                    c.qualityObjectives(), c.testStrategy(), c.entryCriteria(), c.exitCriteria())));
        } catch (IllegalStateException ex) {
            throw QualityExceptions.qualityPlanImmutable(c.qualityPlanId());
        }
    }
}
