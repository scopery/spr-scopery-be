package com.company.scopery.modules.profitability.ratecard.application.action;

import com.company.scopery.modules.profitability.ratecard.application.response.ProfitRateCardResponse;
import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCardRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component("profitabilityUpdateRateCardAction")
public class UpdateRateCardAction {
    private final ProfitRateCardRepository repo;
    private final ProfitabilityAuthorizationService authorization;

    public UpdateRateCardAction(ProfitRateCardRepository repo,
                                ProfitabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRateCardResponse execute(
            UUID workspaceId,
            UUID id,
            String name,
            String roleName,
            String currency,
            BigDecimal amountPerHour,
            BigDecimal amountPerDay) {
        var rateCard = repo.findById(id)
                .orElseThrow(() -> ProfitabilityExceptions.rateCardNotFound(id));
        if (rateCard.projectId() != null) {
            authorization.requireUpdate(rateCard.projectId());
        }
        if ("ARCHIVED".equals(rateCard.status())) {
            throw ProfitabilityExceptions.rateCardInvalid("Cannot update archived rate card");
        }
        return ProfitRateCardResponse.from(repo.save(rateCard.update(name, roleName, currency, amountPerHour, amountPerDay)));
    }
}
