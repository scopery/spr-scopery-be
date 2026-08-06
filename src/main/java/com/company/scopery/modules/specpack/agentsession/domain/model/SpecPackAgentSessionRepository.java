package com.company.scopery.modules.specpack.agentsession.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecPackAgentSessionRepository {

    SpecPackAgentSession save(SpecPackAgentSession session);

    Optional<SpecPackAgentSession> findById(UUID sessionId);

    List<SpecPackAgentSession> findAllByProjectId(UUID projectId);

    SpecPackAgentStage saveStage(SpecPackAgentStage stage);

    List<SpecPackAgentStage> findStagesBySessionId(UUID sessionId);

    Optional<SpecPackAgentStage> findStageBySessionIdAndCode(UUID sessionId, String stageCode);
}
