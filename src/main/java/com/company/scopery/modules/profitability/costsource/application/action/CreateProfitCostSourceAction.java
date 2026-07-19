package com.company.scopery.modules.profitability.costsource.application.action;

import com.company.scopery.modules.profitability.costsource.application.response.ProfitCostSourceResponse;
import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSource;
import com.company.scopery.modules.profitability.costsource.domain.model.ProfitCostSourceRepository;
import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateProfitCostSourceAction {
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitCostSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public CreateProfitCostSourceAction(ProjectProfitabilityProfileRepository profiles,
                                        ProfitCostSourceRepository sources,
                                        ProfitabilityAuthorizationService authorization) {
        this.profiles = profiles;
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitCostSourceResponse execute(
            UUID projectId,
            String sourceType,
            UUID sourceId,
            BigDecimal effortHours,
            BigDecimal rateAmount,
            BigDecimal amount,
            String currency,
            boolean includedInForecast) {
        authorization.requireUpdate(projectId);
        var profile = profiles.findByProjectId(projectId)
                .orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        try {
            return ProfitCostSourceResponse.from(sources.save(ProfitCostSource.create(
                    profile.workspaceId(), projectId, profile.id(), sourceType, sourceId, effortHours, rateAmount,
                    amount, currency, includedInForecast)));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.invalidCostSource(ex.getMessage());
        }
    }
}
