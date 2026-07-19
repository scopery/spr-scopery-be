package com.company.scopery.modules.aiagent.tool.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolExecutionStatus;

import java.util.Optional;
import java.util.UUID;

public interface AiToolExecutionRepository {

    AiToolExecution save(AiToolExecution execution);

    Optional<AiToolExecution> findById(UUID id);

    PageResult<AiToolExecution> findAll(UUID toolId, UUID agentId, AiToolExecutionStatus status, PageQuery pageQuery);
}
