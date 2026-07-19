package com.company.scopery.modules.aiagent.tool.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import com.company.scopery.modules.aiagent.tool.application.query.SearchAiToolExecutionQuery;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolExecutionResponse;
import com.company.scopery.modules.aiagent.tool.application.service.AiToolQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AI Agent - Tool Executions", description = "AiToolExecution audit log queries")
@RestController
@RequestMapping(AiAgentApiPaths.TOOL_EXECUTIONS)
public class AiToolExecutionController {

    private final AiToolQueryService queryService;

    public AiToolExecutionController(AiToolQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Search AI tool execution logs")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AiToolExecutionResponse>>> searchExecutions(
            @RequestParam(required = false) UUID toolId,
            @RequestParam(required = false) UUID agentId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<AiToolExecutionResponse> result = queryService.searchExecutions(
                new SearchAiToolExecutionQuery(toolId, agentId, status, page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }
}
