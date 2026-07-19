package com.company.scopery.modules.profitability.costsource.application.action;

import com.company.scopery.modules.profitability.costsource.application.response.ProfitCostSourceResponse;
import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSourceRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveProfitCostSourceAction {
    private final ProfitCostSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public ArchiveProfitCostSourceAction(ProfitCostSourceRepository sources,
                                         ProfitabilityAuthorizationService authorization) {
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitCostSourceResponse execute(UUID projectId, UUID sourceId) {
        authorization.requireUpdate(projectId);
        var existing = sources.findByIdAndProjectId(sourceId, projectId)
                .orElseThrow(() -> ProfitabilityExceptions.costSourceNotFound(sourceId));
        return ProfitCostSourceResponse.from(sources.save(existing.archive()));
    }
}
