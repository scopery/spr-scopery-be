package com.company.scopery.modules.aiagent.agent.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentRepository {

    Agent save(Agent agent);

    Optional<Agent> findById(UUID id);

    boolean existsByCode(AgentCode code);

    List<Agent> findAllByStatus(AgentStatus status);

    PageResult<Agent> findAll(String keyword, AgentType type, AgentStatus status,
                        AgentOutputFormat outputFormat, PageQuery pageQuery);
}
