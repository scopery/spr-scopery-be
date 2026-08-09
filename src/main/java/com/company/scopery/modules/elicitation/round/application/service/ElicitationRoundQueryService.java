package com.company.scopery.modules.elicitation.round.application.service;

import com.company.scopery.modules.elicitation.round.application.response.ElicitationRoundResponse;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRoundRepository;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ElicitationRoundQueryService {

    private final ElicitationRoundRepository roundRepository;

    public ElicitationRoundQueryService(ElicitationRoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    @Transactional(readOnly = true)
    public ElicitationRoundResponse getById(UUID roundId) {
        return roundRepository.findById(roundId)
                .map(ElicitationRoundResponse::from)
                .orElseThrow(() -> ElicitationExceptions.roundNotFound(roundId));
    }

    @Transactional(readOnly = true)
    public List<ElicitationRoundResponse> listBySession(UUID sessionId) {
        return roundRepository.findAllBySessionId(sessionId)
                .stream()
                .map(ElicitationRoundResponse::from)
                .toList();
    }
}
