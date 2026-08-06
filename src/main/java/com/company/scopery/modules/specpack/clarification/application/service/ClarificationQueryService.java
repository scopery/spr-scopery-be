package com.company.scopery.modules.specpack.clarification.application.service;

import com.company.scopery.modules.specpack.clarification.application.response.ClarificationResponse;
import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarificationRepository;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClarificationQueryService {

    private final SpecPackClarificationRepository clarificationRepository;

    public ClarificationQueryService(SpecPackClarificationRepository clarificationRepository) {
        this.clarificationRepository = clarificationRepository;
    }

    @Transactional(readOnly = true)
    public List<ClarificationResponse> listBySession(UUID sessionId) {
        return clarificationRepository.findAllBySessionId(sessionId).stream()
                .map(ClarificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClarificationResponse> listBySessionAndStatus(UUID sessionId, String status) {
        return clarificationRepository.findAllBySessionIdAndStatus(sessionId, status).stream()
                .map(ClarificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClarificationResponse getById(UUID clarificationId) {
        return clarificationRepository.findById(clarificationId)
                .map(ClarificationResponse::from)
                .orElseThrow(() -> SpecPackExceptions.clarificationNotFound(clarificationId));
    }
}
