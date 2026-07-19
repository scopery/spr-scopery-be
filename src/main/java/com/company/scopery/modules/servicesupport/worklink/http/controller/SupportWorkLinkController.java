package com.company.scopery.modules.servicesupport.worklink.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.worklink.application.action.CreateSupportWorkLinkAction;
import com.company.scopery.modules.servicesupport.worklink.application.command.CreateWorkLinkCommand;
import com.company.scopery.modules.servicesupport.worklink.application.response.SupportWorkLinkResponse;
import com.company.scopery.modules.servicesupport.worklink.application.service.SupportWorkLinkQueryService;
import com.company.scopery.modules.servicesupport.worklink.http.request.CreateWorkLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Work Links")
public class SupportWorkLinkController {
    private final SupportWorkLinkQueryService query;
    private final CreateSupportWorkLinkAction createAction;

    public SupportWorkLinkController(SupportWorkLinkQueryService query, CreateSupportWorkLinkAction createAction) {
        this.query = query; this.createAction = createAction;
    }

    @GetMapping(SupportApiPaths.WORK_LINKS)
    @Operation(summary = "List work links in workspace")
    public ApiResponse<List<SupportWorkLinkResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.WORK_LINKS)
    @Operation(summary = "Create work link")
    public ApiResponse<SupportWorkLinkResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateWorkLinkRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateWorkLinkCommand(req.supportCaseId(), req.targetObjectType(),
                        req.targetObjectId(), req.linkType())));
    }
}
