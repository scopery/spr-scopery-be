package com.company.scopery.modules.clientportal.account.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.account.application.action.DeactivatePortalAccountAction;
import com.company.scopery.modules.clientportal.account.application.action.SuspendPortalAccountAction;
import com.company.scopery.modules.clientportal.account.application.command.DeactivatePortalAccountCommand;
import com.company.scopery.modules.clientportal.account.application.command.SuspendPortalAccountCommand;
import com.company.scopery.modules.clientportal.account.application.response.ExternalPortalAccountResponse;
import com.company.scopery.modules.clientportal.account.application.service.ExternalPortalAccountQueryService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_ACCOUNTS)
@Tag(name = "Client Portal - Accounts")
public class ExternalPortalAccountController {
    private final SuspendPortalAccountAction suspend;
    private final DeactivatePortalAccountAction deactivate;
    private final ExternalPortalAccountQueryService query;
    public ExternalPortalAccountController(SuspendPortalAccountAction suspend, DeactivatePortalAccountAction deactivate,
                                           ExternalPortalAccountQueryService query) {
        this.suspend = suspend; this.deactivate = deactivate; this.query = query;
    }
    @GetMapping("/{accountId}") @Operation(summary = "Get portal account")
    public ApiResponse<ExternalPortalAccountResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID accountId) {
        return ApiResponse.success(query.get(workspaceId, accountId));
    }
    @PostMapping("/{accountId}/suspend") @Operation(summary = "Suspend portal account")
    public ApiResponse<ExternalPortalAccountResponse> suspend(@PathVariable UUID workspaceId, @PathVariable UUID accountId) {
        return ApiResponse.success(suspend.execute(new SuspendPortalAccountCommand(workspaceId, accountId)));
    }
    @PostMapping("/{accountId}/deactivate") @Operation(summary = "Deactivate portal account")
    public ApiResponse<ExternalPortalAccountResponse> deactivate(@PathVariable UUID workspaceId, @PathVariable UUID accountId) {
        return ApiResponse.success(deactivate.execute(new DeactivatePortalAccountCommand(workspaceId, accountId)));
    }
}
