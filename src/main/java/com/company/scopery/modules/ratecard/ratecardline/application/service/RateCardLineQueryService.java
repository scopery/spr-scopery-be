package com.company.scopery.modules.ratecard.ratecardline.application.service;

import com.company.scopery.modules.ratecard.ratecard.domain.model.RateCardRepository;
import com.company.scopery.modules.ratecard.ratecardline.application.response.RateCardLineResponse;
import com.company.scopery.modules.ratecard.ratecardline.domain.model.RateCardLineRepository;
import com.company.scopery.modules.ratecard.ratecardversion.domain.model.RateCardVersionRepository;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RateCardLineQueryService {
    private final RateCardRepository rateCardRepository;
    private final RateCardVersionRepository versionRepository;
    private final RateCardLineRepository lineRepository;
    private final RateCardAuthorizationService authorizationService;

    public RateCardLineQueryService(RateCardRepository rateCardRepository, RateCardVersionRepository versionRepository,
                                    RateCardLineRepository lineRepository,
                                    RateCardAuthorizationService authorizationService) {
        this.rateCardRepository = rateCardRepository; this.versionRepository = versionRepository;
        this.lineRepository = lineRepository; this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public List<RateCardLineResponse> list(UUID rateCardId, UUID versionId) {
        var card = rateCardRepository.findById(rateCardId)
                .orElseThrow(() -> RateCardExceptions.rateCardNotFound(rateCardId));
        authorizationService.requireRateCardLineView(card.workspaceId());
        var version = versionRepository.findById(versionId)
                .orElseThrow(() -> RateCardExceptions.versionNotFound(versionId));
        if (!version.rateCardId().equals(rateCardId)) throw RateCardExceptions.versionNotFound(versionId);
        return lineRepository.findByVersionId(versionId).stream().map(RateCardLineResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RateCardLineResponse get(UUID rateCardId, UUID versionId, UUID lineId) {
        return list(rateCardId, versionId).stream().filter(l -> l.id().equals(lineId)).findFirst()
                .orElseThrow(() -> RateCardExceptions.lineNotFound(lineId));
    }
}
