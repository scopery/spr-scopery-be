package com.company.scopery.modules.aiagent.tool.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.valueobject.AiToolCode;

import java.util.Optional;
import java.util.UUID;

public interface AiToolRepository {

    AiTool save(AiTool tool);

    Optional<AiTool> findById(UUID id);

    Optional<AiTool> findByCode(AiToolCode code);

    boolean existsByCode(AiToolCode code);

    PageResult<AiTool> findAll(String category, AiToolStatus status, String codeOrName, PageQuery pageQuery);
}
