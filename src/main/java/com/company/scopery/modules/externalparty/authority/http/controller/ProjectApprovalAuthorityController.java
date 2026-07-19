package com.company.scopery.modules.externalparty.authority.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.authority.application.action.CreateProjectApprovalAuthorityAction;
import com.company.scopery.modules.externalparty.authority.application.command.CreateProjectApprovalAuthorityCommand;
import com.company.scopery.modules.externalparty.authority.application.response.ProjectApprovalAuthorityResponse;
import com.company.scopery.modules.externalparty.authority.application.service.ProjectApprovalAuthorityQueryService;
import com.company.scopery.modules.externalparty.authority.http.request.CreateProjectApprovalAuthorityRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ExternalPartyApiPaths.APPROVAL_AUTHORITIES)
@Tag(name = "External Party - Approval Authorities")
public class ProjectApprovalAuthorityController {
    private final CreateProjectApprovalAuthorityAction create;
    private final ProjectApprovalAuthorityQueryService query;
    public ProjectApprovalAuthorityController(CreateProjectApprovalAuthorityAction create, ProjectApprovalAuthorityQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create approval authority")
    public ApiResponse<ProjectApprovalAuthorityResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateProjectApprovalAuthorityRequest r) {
        return ApiResponse.success(create.execute(new CreateProjectApprovalAuthorityCommand(projectId, r.stakeholderId(), r.authorityType(), r.notes())));
    }
    @GetMapping @Operation(summary = "List approval authorities")
    public ApiResponse<List<ProjectApprovalAuthorityResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
}
