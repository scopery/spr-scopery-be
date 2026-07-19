package com.company.scopery.modules.profitability.adjustment.application.action;
import com.company.scopery.modules.profitability.adjustment.application.response.ProfitAdjustmentResponse;
import com.company.scopery.modules.profitability.adjustment.domain.model.*;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.util.UUID;
@Component
public class CreateProfitAdjustmentAction {
    private final ProjectProfitabilityProfileRepository profiles; private final ProfitAdjustmentRepository adjustments; private final ProfitabilityAuthorizationService authorization;
    public CreateProfitAdjustmentAction(ProjectProfitabilityProfileRepository profiles, ProfitAdjustmentRepository adjustments, ProfitabilityAuthorizationService authorization) {
        this.profiles=profiles; this.adjustments=adjustments; this.authorization=authorization;
    }
    @Transactional
    public ProfitAdjustmentResponse execute(UUID projectId, String type, BigDecimal amount, String reason) {
        authorization.requireUpdate(projectId);
        var profile = profiles.findByProjectId(projectId).orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        return ProfitAdjustmentResponse.from(adjustments.save(ProfitAdjustment.create(profile.workspaceId(), projectId, profile.id(), type, amount, reason)));
    }
}
