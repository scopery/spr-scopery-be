package com.company.scopery.modules.profitability.ratecard.application.action;

import com.company.scopery.modules.profitability.ratecard.application.response.ProfitRateCardResponse;
import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCard;
import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCardRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component("profitabilityCreateRateCardAction")
public class CreateRateCardAction {
    private final ProfitRateCardRepository repo;
    private final ProfitabilityAuthorizationService authorization;

    public CreateRateCardAction(ProfitRateCardRepository repo,
                                ProfitabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRateCardResponse execute(
            UUID workspaceId,
            UUID projectId,
            String rateCode,
            String name,
            String rateType,
            String roleName,
            UUID teamId,
            String currency,
            BigDecimal amountPerHour,
            BigDecimal amountPerDay) {
        if (projectId != null) {
            authorization.requireUpdate(projectId);
        }
        if (repo.existsByWorkspaceIdAndProjectIdAndRateCode(workspaceId, projectId, rateCode)) {
            throw ProfitabilityExceptions.rateCardCodeExists(rateCode);
        }
        try {
            ProfitRateCard rateCard = ProfitRateCard.create(
                    workspaceId, projectId, rateCode, name, rateType, roleName, teamId,
                    currency, amountPerHour, amountPerDay);
            return ProfitRateCardResponse.from(repo.save(rateCard));
        } catch (IllegalArgumentException ex) {
            throw ProfitabilityExceptions.rateCardInvalid(ex.getMessage());
        }
    }
}
