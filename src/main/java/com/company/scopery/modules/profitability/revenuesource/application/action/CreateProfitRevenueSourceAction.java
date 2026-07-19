package com.company.scopery.modules.profitability.revenuesource.application.action;

import com.company.scopery.modules.profitability.profile.domain.model.ProjectProfitabilityProfileRepository;
import com.company.scopery.modules.profitability.revenuesource.application.response.ProfitRevenueSourceResponse;
import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSource;
import com.company.scopery.modules.profitability.revenuesource.domain.model.ProfitRevenueSourceRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateProfitRevenueSourceAction {
    private final ProjectProfitabilityProfileRepository profiles;
    private final ProfitRevenueSourceRepository sources;
    private final ProfitabilityAuthorizationService authorization;

    public CreateProfitRevenueSourceAction(ProjectProfitabilityProfileRepository profiles,
                                           ProfitRevenueSourceRepository sources,
                                           ProfitabilityAuthorizationService authorization) {
        this.profiles = profiles;
        this.sources = sources;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRevenueSourceResponse execute(
            UUID projectId,
            String sourceType,
            UUID sourceId,
            BigDecimal amount,
            String currency,
            boolean includedInForecast,
            String confidence) {
        authorization.requireUpdate(projectId);
        var profile = profiles.findByProjectId(projectId)
                .orElseThrow(() -> ProfitabilityExceptions.profileNotFound(projectId));
        try {
            return ProfitRevenueSourceResponse.from(sources.save(ProfitRevenueSource.create(
                    profile.workspaceId(), projectId, profile.id(), sourceType, sourceId, amount, currency,
                    includedInForecast, confidence)));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.invalidRevenueSource(ex.getMessage());
        }
    }
}
