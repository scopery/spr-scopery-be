package com.company.scopery.modules.clientportal.invite.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.invite.application.action.CreatePortalInviteAction;
import com.company.scopery.modules.clientportal.invite.application.command.CreatePortalInviteCommand;
import com.company.scopery.modules.clientportal.invite.application.response.ExternalPortalInviteResponse;
import com.company.scopery.modules.clientportal.invite.application.service.PortalInviteQueryService;
import com.company.scopery.modules.clientportal.invite.http.request.CreatePortalInviteRequest;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.INVITES)
@Tag(name = "Client Portal - Invites")
public class PortalInviteController {
    private final CreatePortalInviteAction create;
    private final PortalInviteQueryService query;
    public PortalInviteController(CreatePortalInviteAction create, PortalInviteQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create portal invite")
    public ApiResponse<ExternalPortalInviteResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreatePortalInviteRequest r) {
        return ApiResponse.success(create.execute(new CreatePortalInviteCommand(projectId, r.email(), r.expiresInDays())));
    }
    @GetMapping @Operation(summary = "List portal invites")
    public ApiResponse<List<ExternalPortalInviteResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
}
