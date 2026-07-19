package com.company.scopery.modules.ratecard.ratecard.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.ratecard.application.query.SearchRateCardQuery;
import com.company.scopery.modules.ratecard.ratecard.application.response.RateCardResponse;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardStatus;
import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.util.RateCardEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RateCardQueryService {
    private final RateCardRepository rateCardRepository;
    private final RateCardAuthorizationService authorizationService;

    public RateCardQueryService(RateCardRepository rateCardRepository,
                                RateCardAuthorizationService authorizationService) {
        this.rateCardRepository = rateCardRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public RateCardResponse get(UUID id) {
        var card = rateCardRepository.findById(id).orElseThrow(() -> RateCardExceptions.rateCardNotFound(id));
        authorizationService.requireRateCardView(card.workspaceId());
        return RateCardResponse.from(card);
    }

    @Transactional(readOnly = true)
    public PageResult<RateCardResponse> search(SearchRateCardQuery query) {
        authorizationService.requireRateCardView(query.workspaceId());
        RateCardScope scope = RateCardEnumParser.parseOptional(RateCardScope.class, query.scope(), "RATE_CARD_INVALID_SCOPE", "scope");
        RateCardStatus status = RateCardEnumParser.parseOptional(RateCardStatus.class, query.status(), "VALIDATION_ERROR", "status");
        return rateCardRepository.search(scope, query.organizationId(), query.workspaceId(), status,
                        query.currency(), query.code(), new PageQuery(query.page(), query.size(), "createdAt", false))
                .map(RateCardResponse::from);
    }
}
