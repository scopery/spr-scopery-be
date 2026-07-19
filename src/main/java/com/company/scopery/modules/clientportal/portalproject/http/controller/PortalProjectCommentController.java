package com.company.scopery.modules.clientportal.portalproject.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.comment.application.action.CreateClientCommentAction;
import com.company.scopery.modules.clientportal.comment.application.command.CreateClientCommentCommand;
import com.company.scopery.modules.clientportal.comment.application.response.ClientCommentResponse;
import com.company.scopery.modules.clientportal.comment.application.service.ClientCommentQueryService;
import com.company.scopery.modules.clientportal.comment.http.request.CreateClientCommentRequest;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_PROJECTS + "/{projectId}/comments")
@Tag(name = "Client Portal - Project Comments")
public class PortalProjectCommentController {
    private final CreateClientCommentAction create;
    private final ClientCommentQueryService query;
    public PortalProjectCommentController(CreateClientCommentAction create, ClientCommentQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Add comment as portal user")
    public ApiResponse<ClientCommentResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateClientCommentRequest r) {
        return ApiResponse.success(create.execute(new CreateClientCommentCommand(projectId, r.targetType(), r.targetId(), r.body())));
    }
    @GetMapping @Operation(summary = "List comments for granted project")
    public ApiResponse<List<ClientCommentResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listPortal(projectId));
    }
}
