package com.company.scopery.modules.aiagent.tool.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.tool.application.query.GetAiToolDetailQuery;
import com.company.scopery.modules.aiagent.tool.application.query.SearchAiToolExecutionQuery;
import com.company.scopery.modules.aiagent.tool.application.query.SearchAiToolQuery;
import com.company.scopery.modules.aiagent.tool.application.response.AiAgentToolBindingResponse;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolDetailResponse;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolExecutionResponse;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolResponse;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolExecutionStatus;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.*;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiToolQueryService {

    private final AiToolRepository toolRepository;
    private final AiToolPermissionRepository permissionRepository;
    private final AiAgentToolBindingRepository bindingRepository;
    private final AiToolExecutionRepository executionRepository;

    public AiToolQueryService(AiToolRepository toolRepository,
                              AiToolPermissionRepository permissionRepository,
                              AiAgentToolBindingRepository bindingRepository,
                              AiToolExecutionRepository executionRepository) {
        this.toolRepository = toolRepository;
        this.permissionRepository = permissionRepository;
        this.bindingRepository = bindingRepository;
        this.executionRepository = executionRepository;
    }

    @Transactional(readOnly = true)
    public AiToolDetailResponse getDetail(GetAiToolDetailQuery query) {
        AiTool tool = findOrThrow(query.id());
        return AiToolDetailResponse.from(tool, permissionRepository.findByToolId(tool.id()));
    }

    @Transactional(readOnly = true)
    public PageResult<AiToolResponse> search(SearchAiToolQuery query) {
        AiToolStatus status = AiAgentEnumParser.parseOptional(
                AiToolStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_AI_TOOL_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);
        return toolRepository.findAll(query.category(), status, query.codeOrName(), pageQuery)
                .map(AiToolResponse::from);
    }

    @Transactional(readOnly = true)
    public List<AiAgentToolBindingResponse> listBindings(UUID toolId) {
        findOrThrow(toolId);
        return bindingRepository.findByToolId(toolId).stream()
                .map(AiAgentToolBindingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResult<AiToolExecutionResponse> searchExecutions(SearchAiToolExecutionQuery query) {
        AiToolExecutionStatus status = AiAgentEnumParser.parseOptional(
                AiToolExecutionStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_AI_TOOL_EXECUTION_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);
        return executionRepository.findAll(query.toolId(), query.agentId(), status, pageQuery)
                .map(AiToolExecutionResponse::from);
    }

    private AiTool findOrThrow(UUID id) {
        return toolRepository.findById(id).orElseThrow(() -> AiAgentExceptions.aiToolNotFound(id));
    }
}
