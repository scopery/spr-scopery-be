package com.company.scopery.modules.profitability.revenuesource.application.action;

import com.company.scopery.modules.profitability.revenuesource.application.response.ProfitRevenueSourceResponse;
import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSourceRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class UpdateProfitRevenueSourceAction {
    private final ProfitRevenueSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public UpdateProfitRevenueSourceAction(ProfitRevenueSourceRepository sources,
                                           ProfitabilityAuthorizationService authorization) {
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRevenueSourceResponse execute(
            UUID projectId,
            UUID sourceId,
            String sourceType,
            UUID linkedSourceId,
            BigDecimal amount,
            String currency,
            boolean includedInForecast,
            String confidence) {
        authorization.requireUpdate(projectId);
        var existing = sources.findByIdAndProjectId(sourceId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.revenueSourceNotFound(sourceId));
        if ("ARCHIVED".equals(existing.status())) {
            throw ProfitabilityExceptions.revenueSourceArchived(sourceId);
        }
        try {
            return ProfitRevenueSourceResponse.from(sources.save(existing.update(
                    sourceType, linkedSourceId, amount, currency, includedInForecast, confidence)));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.invalidRevenueSource(ex.getMessage());
        }
    }
}
