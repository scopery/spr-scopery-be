package com.company.scopery.modules.servicesupport.knowledgelink.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.knowledgelink.application.action.CreateSupportKnowledgeLinkAction;
import com.company.scopery.modules.servicesupport.knowledgelink.application.command.CreateKnowledgeLinkCommand;
import com.company.scopery.modules.servicesupport.knowledgelink.application.response.SupportKnowledgeLinkResponse;
import com.company.scopery.modules.servicesupport.knowledgelink.application.service.SupportKnowledgeLinkQueryService;
import com.company.scopery.modules.servicesupport.knowledgelink.http.request.CreateKnowledgeLinkRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Knowledge Links")
public class SupportKnowledgeLinkController {
    private final SupportKnowledgeLinkQueryService query;
    private final CreateSupportKnowledgeLinkAction createAction;

    public SupportKnowledgeLinkController(SupportKnowledgeLinkQueryService query,
            CreateSupportKnowledgeLinkAction createAction) {
        this.query = query; this.createAction = createAction;
    }

    @GetMapping(SupportApiPaths.KNOWLEDGE_LINKS)
    @Operation(summary = "List knowledge links in workspace")
    public ApiResponse<List<SupportKnowledgeLinkResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.KNOWLEDGE_LINKS)
    @Operation(summary = "Create knowledge link")
    public ApiResponse<SupportKnowledgeLinkResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateKnowledgeLinkRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateKnowledgeLinkCommand(req.supportCaseId(), req.documentId(),
                        req.linkType(), Boolean.TRUE.equals(req.clientVisible()))));
    }
}
