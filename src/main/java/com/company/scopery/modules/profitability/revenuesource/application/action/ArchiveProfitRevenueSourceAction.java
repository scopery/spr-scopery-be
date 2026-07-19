package com.company.scopery.modules.profitability.revenuesource.application.action;

import com.company.scopery.modules.profitability.revenuesource.application.response.ProfitRevenueSourceResponse;
import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSourceRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveProfitRevenueSourceAction {
    private final ProfitRevenueSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public ArchiveProfitRevenueSourceAction(ProfitRevenueSourceRepository sources,
                                            ProfitabilityAuthorizationService authorization) {
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRevenueSourceResponse execute(UUID projectId, UUID sourceId) {
        authorization.requireUpdate(projectId);
        var existing = sources.findByIdAndProjectId(sourceId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.revenueSourceNotFound(sourceId));
        return ProfitRevenueSourceResponse.from(sources.save(existing.archive()));
    }
}
