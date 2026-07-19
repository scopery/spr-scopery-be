package com.company.scopery.modules.externalparty.stakeholder.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import com.company.scopery.modules.externalparty.stakeholder.application.action.CreateProjectStakeholderAction;
import com.company.scopery.modules.externalparty.stakeholder.application.command.CreateProjectStakeholderCommand;
import com.company.scopery.modules.externalparty.stakeholder.application.response.ProjectStakeholderResponse;
import com.company.scopery.modules.externalparty.stakeholder.application.service.ProjectStakeholderQueryService;
import com.company.scopery.modules.externalparty.stakeholder.http.request.CreateProjectStakeholderRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ExternalPartyApiPaths.STAKEHOLDERS) @Tag(name="External Party - Stakeholders")
public class ProjectStakeholderController {
    private final CreateProjectStakeholderAction create; private final ProjectStakeholderQueryService query;
    public ProjectStakeholderController(CreateProjectStakeholderAction create, ProjectStakeholderQueryService query){this.create=create;this.query=query;}
    @PostMapping @Operation(summary="Create stakeholder")
    public ApiResponse<ProjectStakeholderResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateProjectStakeholderRequest r) {
        return ApiResponse.success(create.execute(new CreateProjectStakeholderCommand(projectId, r.contactId(), r.organizationId(), r.internalUserId(), r.stakeholderRole(), Boolean.TRUE.equals(r.clientFacing()))));
    }
    @GetMapping @Operation(summary="List stakeholders")
    public ApiResponse<List<ProjectStakeholderResponse>> list(@PathVariable UUID projectId){return ApiResponse.success(query.list(projectId));}
    @GetMapping("/{stakeholderId}") @Operation(summary="Get stakeholder")
    public ApiResponse<ProjectStakeholderResponse> get(@PathVariable UUID projectId, @PathVariable UUID stakeholderId){return ApiResponse.success(query.get(projectId, stakeholderId));}
}
