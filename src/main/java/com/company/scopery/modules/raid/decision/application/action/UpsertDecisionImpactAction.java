package com.company.scopery.modules.raid.decision.application.action;

import com.company.scopery.modules.raid.decision.application.command.UpsertDecisionImpactCommand;
import com.company.scopery.modules.raid.decision.application.response.DecisionImpactResponse;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpact;
import com.company.scopery.modules.raid.decision.domain.model.DecisionImpactRepository;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecordRepository;
import com.company.scopery.modules.raid.shared.authorization.RaidAuthorizationService;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpsertDecisionImpactAction {
    private final DecisionRecordRepository decisions;
    private final DecisionImpactRepository impacts;
    private final RaidAuthorizationService authorization;

    public UpsertDecisionImpactAction(DecisionRecordRepository decisions, DecisionImpactRepository impacts,
                                      RaidAuthorizationService authorization) {
        this.decisions = decisions;
        this.impacts = impacts;
        this.authorization = authorization;
    }

    @Transactional
    public DecisionImpactResponse execute(UpsertDecisionImpactCommand command) {
        authorization.requireDecisionUpdate(command.projectId());
        decisions.findByIdAndProjectId(command.decisionId(), command.projectId())
                .orElseThrow(() -> RaidExceptions.decisionNotFound(command.decisionId()));
        DecisionImpact existing = impacts.findByDecisionId(command.decisionId()).orElse(null);
        DecisionImpact impact = DecisionImpact.create(
                command.decisionId(), command.projectId(), command.scopeImpact(), command.scheduleImpactDays(),
                command.estimateHoursImpact(), command.costImpact(), command.revenueImpact(), command.marginImpact(),
                command.riskImpact(), command.deliverableImpact(), command.acceptanceImpact());
        if (existing != null) {
            impact = new DecisionImpact(existing.id(), command.decisionId(), command.projectId(),
                    impact.scopeImpact(), impact.scheduleImpactDays(), impact.estimateHoursImpact(),
                    impact.costImpact(), impact.revenueImpact(), impact.marginImpact(),
                    impact.riskImpact(), impact.deliverableImpact(), impact.acceptanceImpact(),
                    existing.version(), existing.createdAt());
        }
        impact = impacts.save(impact);
        if (!authorization.canViewFinance(command.projectId())) {
            return DecisionImpactResponse.fromMasked(impact);
        }
        return DecisionImpactResponse.from(impact);
    }
}
