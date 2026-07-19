package com.company.scopery.modules.profitability.costsource.application.action;

import com.company.scopery.modules.profitability.costsource.application.response.ProfitCostSourceResponse;
import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSourceRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class UpdateProfitCostSourceAction {
    private final ProfitCostSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public UpdateProfitCostSourceAction(ProfitCostSourceRepository sources,
                                        ProfitabilityAuthorizationService authorization) {
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitCostSourceResponse execute(
            UUID projectId,
            UUID sourceId,
            String sourceType,
            UUID linkedSourceId,
            BigDecimal effortHours,
            BigDecimal rateAmount,
            BigDecimal amount,
            String currency,
            boolean includedInForecast) {
        authorization.requireUpdate(projectId);
        var existing = sources.findByIdAndProjectId(sourceId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.costSourceNotFound(sourceId));
        if ("ARCHIVED".equals(existing.status())) {
            throw ProfitabilityExceptions.costSourceArchived(sourceId);
        }
        try {
            return ProfitCostSourceResponse.from(sources.save(existing.update(
                    sourceType, linkedSourceId, effortHours, rateAmount, amount, currency, includedInForecast)));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.invalidCostSource(ex.getMessage());
        }
    }
}
