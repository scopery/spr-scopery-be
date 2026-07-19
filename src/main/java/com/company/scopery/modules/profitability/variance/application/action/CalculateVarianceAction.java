package com.company.scopery.modules.profitability.variance.application.action;

import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import com.company.scopery.modules.profitability.variance.application.response.ProfitVarianceResponse;
import com.company.scopery.modules.profitability.variance.domain.model.ProfitVariance;
import com.company.scopery.modules.profitability.variance.domain.model.ProfitVarianceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CalculateVarianceAction {
    private final ProfitVarianceRepository variances;
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitabilityAuthorizationService authorization;

    public CalculateVarianceAction(ProfitVarianceRepository variances,
                                   ProjectProfitabilityProfileRepository profiles,
                                   ProfitabilityAuthorizationService authorization) {
        this.variances = variances;
        this.profiles = profiles;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitVarianceResponse execute(
            UUID projectId,
            String varianceType,
            BigDecimal fromAmount,
            BigDecimal toAmount,
            BigDecimal varianceAmount,
            BigDecimal variancePercent,
            String currency,
            String explanation,
            UUID sourceSnapshotId) {
        authorization.requireUpdate(projectId);
        var profile = profiles.findByProjectId(projectId)
                .orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        try {
            return ProfitVarianceResponse.from(variances.save(ProfitVariance.create(
                    profile.workspaceId(), projectId, profile.id(),
                    varianceType, fromAmount, toAmount, varianceAmount, variancePercent,
                    currency, explanation, sourceSnapshotId)));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.varianceCalculationFailed(ex.getMessage());
        }
    }
}
