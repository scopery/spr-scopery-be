package com.company.scopery.modules.ratecard.ratecardversion.application.service;

import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardversion.application.response.RateCardVersionResponse;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersionRepository;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RateCardVersionQueryService {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardAuthorizationService authorizationService;

    public RateCardVersionQueryService(RateCardRepository rateCardRepository,
                                       RateCardVersionRepository versionRepository,
                                       RateCardAuthorizationService authorizationService) {
        this.rateCardRepository = rateCardRepository;
        this.versionRepository = versionRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public RateCardVersionResponse get(UUID rateCardId, UUID versionId) {
        var card = rateCardRepository.findById(rateCardId)
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(rateCardId));
        authorizationService.requireRateCardView(card.workspaceId());
        var version = versionRepository.findById(versionId)
                .orElseThrow(() -> RateCardExceptions.versionNotFound(versionId));
        if (!version.rateCardId().equals(rateCardId)) throw RateCardExceptions.versionNotFound(versionId);
        return RateCardVersionResponse.from(version);
    }

    @Transactional(readOnly = true)
    public List<RateCardVersionResponse> list(UUID rateCardId) {
        var card = rateCardRepository.findById(rateCardId)
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(rateCardId));
        authorizationService.requireRateCardView(card.workspaceId());
        return versionRepository.findByRateCardId(rateCardId).stream().map(RateCardVersionResponse::from).toList();
    }
}
