package com.company.scopery.modules.aiagent.prompt.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.valueobject.PromptTemplateCode;

import java.util.Optional;
import java.util.UUID;

public interface PromptTemplateRepository {

    PromptTemplate save(PromptTemplate template);

    Optional<PromptTemplate> findById(UUID id);

    boolean existsByAgentIdAndCode(UUID agentId, PromptTemplateCode code);

    PageResult<PromptTemplate> findAll(UUID agentId, String keyword, PromptTemplateStatus status,
                                       PageQuery pageQuery);
}
