package com.company.scopery.modules.servicesupport.queue.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.queue.application.action.CreateSupportQueueAction;
import com.company.scopery.modules.servicesupport.queue.application.command.CreateSupportQueueCommand;
import com.company.scopery.modules.servicesupport.queue.application.response.SupportQueueResponse;
import com.company.scopery.modules.servicesupport.queue.application.service.SupportQueueQueryService;
import com.company.scopery.modules.servicesupport.queue.http.request.CreateSupportQueueRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Queues")
public class SupportQueueController {
    private final SupportQueueQueryService query;
    private final CreateSupportQueueAction createAction;

    public SupportQueueController(SupportQueueQueryService query, CreateSupportQueueAction createAction) {
        this.query = query; this.createAction = createAction;
    }

    @GetMapping(SupportApiPaths.QUEUES)
    @Operation(summary = "List support queues")
    public ApiResponse<List<SupportQueueResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.QUEUES)
    @Operation(summary = "Create support queue")
    public ApiResponse<SupportQueueResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateSupportQueueRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateSupportQueueCommand(req.queueCode(), req.name())));
    }
}
