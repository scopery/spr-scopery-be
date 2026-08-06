package com.company.scopery.modules.specpack.agentsession.application.service;

import com.company.scopery.modules.specpack.agentsession.application.response.AgentSessionResponse;
import com.company.scopery.modules.specpack.agentsession.application.response.AgentStageResponse;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSessionRepository;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AgentSessionQueryService {

    private final SpecPackAgentSessionRepository sessionRepository;

    public AgentSessionQueryService(SpecPackAgentSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public AgentSessionResponse getById(UUID projectId, UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .filter(s -> s.projectId().equals(projectId))
                .map(AgentSessionResponse::from)
                .orElseThrow(() -> SpecPackExceptions.agentSessionNotFound(sessionId));
    }

    @Transactional(readOnly = true)
    public List<AgentSessionResponse> listByProject(UUID projectId) {
        return sessionRepository.findAllByProjectId(projectId).stream()
                .map(AgentSessionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgentStageResponse> getStages(UUID sessionId) {
        return sessionRepository.findStagesBySessionId(sessionId).stream()
                .map(AgentStageResponse::from)
                .toList();
    }
}
