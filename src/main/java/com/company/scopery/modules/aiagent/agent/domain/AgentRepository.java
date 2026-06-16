package com.company.scopery.modules.aiagent.agent.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentRepository {

    Agent save(Agent agent);

    Optional<Agent> findById(UUID id);

    boolean existsByCode(AgentCode code);

    List<Agent> findAllByStatus(AgentStatus status);

    Page<Agent> findAll(String keyword, AgentType type, AgentStatus status,
                        AgentOutputFormat outputFormat, Pageable pageable);
}
