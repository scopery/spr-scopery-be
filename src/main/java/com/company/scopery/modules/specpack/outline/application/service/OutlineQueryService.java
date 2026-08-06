package com.company.scopery.modules.specpack.outline.application.service;

import com.company.scopery.modules.specpack.outline.application.response.OutlineResponse;
import com.company.scopery.modules.specpack.outline.domain.model.SpecPackOutlineRepository;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OutlineQueryService {

    private final SpecPackOutlineRepository outlineRepository;

    public OutlineQueryService(SpecPackOutlineRepository outlineRepository) {
        this.outlineRepository = outlineRepository;
    }

    @Transactional(readOnly = true)
    public List<OutlineResponse> listBySession(UUID sessionId) {
        return outlineRepository.findAllBySessionId(sessionId).stream()
                .map(OutlineResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OutlineResponse getById(UUID outlineId) {
        return outlineRepository.findById(outlineId)
                .map(OutlineResponse::from)
                .orElseThrow(() -> SpecPackExceptions.outlineNotFound(outlineId));
    }
}
