package com.company.scopery.modules.externalparty.organization.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.organization.application.action.CreateExternalOrganizationAction;
import com.company.scopery.modules.externalparty.organization.application.command.CreateExternalOrganizationCommand;
import com.company.scopery.modules.externalparty.organization.application.response.ExternalOrganizationResponse;
import com.company.scopery.modules.externalparty.organization.application.service.ExternalOrganizationQueryService;
import com.company.scopery.modules.externalparty.organization.http.request.CreateExternalOrganizationRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ExternalPartyApiPaths.ORGANIZATIONS)
@Tag(name = "External Party - Organizations")
public class ExternalOrganizationController {
    private final CreateExternalOrganizationAction create;
    private final ExternalOrganizationQueryService query;
    public ExternalOrganizationController(CreateExternalOrganizationAction create, ExternalOrganizationQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create organization")
    public ApiResponse<ExternalOrganizationResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateExternalOrganizationRequest r) {
        return ApiResponse.success(create.execute(new CreateExternalOrganizationCommand(workspaceId, r.code(), r.name(), r.organizationType(), r.taxId(), r.website(), r.notes())));
    }
    @GetMapping @Operation(summary = "List organizations")
    public ApiResponse<List<ExternalOrganizationResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping("/{organizationId}") @Operation(summary = "Get organization")
    public ApiResponse<ExternalOrganizationResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID organizationId) {
        return ApiResponse.success(query.get(workspaceId, organizationId));
    }
}
