package com.company.scopery.modules.profitability.adjustment.application.action;
import com.company.scopery.modules.profitability.adjustment.application.response.ProfitAdjustmentResponse;
import com.company.scopery.modules.profitability.adjustment.domain.model.ProfitAdjustmentRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import com.company.scopery.modules.profitability.shared.event.ProfitabilityRebuildRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class ApplyProfitAdjustmentAction {
    private final ProfitAdjustmentRepository adjustments;
    private final ProfitabilityAuthorizationService authorization;
    private final ApplicationEventPublisher events;

    public ApplyProfitAdjustmentAction(ProfitAdjustmentRepository adjustments,
                                       ProfitabilityAuthorizationService authorization,
                                       ApplicationEventPublisher events) {
        this.adjustments = adjustments; this.authorization = authorization; this.events = events;
    }

    @Transactional
    public ProfitAdjustmentResponse execute(UUID projectId, UUID adjustmentId) {
        authorization.requireUpdate(projectId);
        var adj = adjustments.findByIdAndProjectId(adjustmentId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.adjustmentNotFound(adjustmentId));
        adj = adjustments.save(adj.apply());
        events.publishEvent(new ProfitabilityRebuildRequestedEvent(projectId, "ADJUSTMENT_APPLIED"));
        return ProfitAdjustmentResponse.from(adj);
    }
}
