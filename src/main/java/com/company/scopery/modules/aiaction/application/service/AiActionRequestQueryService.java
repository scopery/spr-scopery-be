package com.company.scopery.modules.aiaction.application.service;

import com.company.scopery.modules.aiaction.application.response.AiActionRequestResponse;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequestRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AiActionRequestQueryService {

    private final AiActionRequestRepository requestRepository;

    public AiActionRequestQueryService(AiActionRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public AiActionRequestResponse getRequest(UUID requestId) {
        AiActionRequest r = requestRepository.findById(requestId)
                .orElseThrow(() -> AiActionExceptions.requestNotFound(requestId));
        return toResponse(r);
    }

    private AiActionRequestResponse toResponse(AiActionRequest r) {
        return new AiActionRequestResponse(
                r.id(), r.workspaceId(), r.projectId(), r.initiatedByUserId(),
                r.originType().name(), r.originSuggestionRef(), r.status().name(),
                r.intentSummary(), r.latestPlanId(), r.createdAt(), r.updatedAt());
    }
}
