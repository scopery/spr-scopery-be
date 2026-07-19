package com.company.scopery.modules.aiagent.tool.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiAgentToolBindingRepository {

    AiAgentToolBinding save(AiAgentToolBinding binding);

    Optional<AiAgentToolBinding> findById(UUID id);

    Optional<AiAgentToolBinding> findByAgentIdAndToolId(UUID agentId, UUID toolId);

    boolean existsByAgentIdAndToolId(UUID agentId, UUID toolId);

    List<AiAgentToolBinding> findByToolId(UUID toolId);

    List<AiAgentToolBinding> findByAgentId(UUID agentId);
}
