package com.company.scopery.modules.elicitation.session.application.service;

import com.company.scopery.modules.elicitation.session.application.response.ElicitationSessionResponse;
import com.company.scopery.modules.elicitation.session.domain.model.ElicitationSessionRepository;
import com.company.scopery.modules.elicitation.shared.error.ElicitationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ElicitationSessionQueryService {

    private final ElicitationSessionRepository sessionRepository;

    public ElicitationSessionQueryService(ElicitationSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public ElicitationSessionResponse getById(UUID projectId, UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .filter(s -> s.projectId().equals(projectId))
                .map(ElicitationSessionResponse::from)
                .orElseThrow(() -> ElicitationExceptions.sessionNotFound(sessionId));
    }

    @Transactional(readOnly = true)
    public List<ElicitationSessionResponse> listByProject(UUID projectId) {
        return sessionRepository.findAllByProjectId(projectId)
                .stream()
                .map(ElicitationSessionResponse::from)
                .toList();
    }
}
