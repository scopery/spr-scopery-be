package com.company.scopery.modules.profitability.ratecard.application.action;

import com.company.scopery.modules.profitability.ratecard.application.response.ProfitRateCardResponse;
import com.company.scopery.modules.profitability.ratecard.domain.model.ProfitRateCardRepository;
import com.company.scopery.modules.profitability.shared.authorization.ProfitabilityAuthorizationService;
import com.company.scopery.modules.profitability.shared.error.ProfitabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("profitabilityArchiveRateCardAction")
public class ArchiveRateCardAction {
    private final ProfitRateCardRepository repo;
    private final ProfitabilityAuthorizationService authorization;

    public ArchiveRateCardAction(ProfitRateCardRepository repo,
                                 ProfitabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public ProfitRateCardResponse execute(UUID workspaceId, UUID id) {
        var rateCard = repo.findById(id)
                .orElseThrow(() -> ProfitabilityExceptions.rateCardNotFound(id));
        if (rateCard.projectId() != null) {
            authorization.requireUpdate(rateCard.projectId());
        }
        try {
            return ProfitRateCardResponse.from(repo.save(rateCard.archive()));
        } catch (IllegalStateException ex) {
            throw ProfitabilityExceptions.rateCardInvalid(ex.getMessage());
        }
    }
}
