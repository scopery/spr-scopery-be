package com.company.scopery.modules.externalparty.relationship.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.externalparty.relationship.application.action.CreateProjectExternalPartyRelationshipAction;
import com.company.scopery.modules.externalparty.relationship.application.command.CreateProjectExternalPartyRelationshipCommand;
import com.company.scopery.modules.externalparty.relationship.application.response.ProjectExternalPartyRelationshipResponse;
import com.company.scopery.modules.externalparty.relationship.application.service.ProjectExternalPartyRelationshipQueryService;
import com.company.scopery.modules.externalparty.relationship.http.request.CreateProjectExternalPartyRelationshipRequest;
import com.company.scopery.modules.externalparty.shared.constant.ExternalPartyApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ExternalPartyApiPaths.RELATIONSHIPS)
@Tag(name = "External Party - Relationships")
public class ProjectExternalPartyRelationshipController {
    private final CreateProjectExternalPartyRelationshipAction create;
    private final ProjectExternalPartyRelationshipQueryService query;
    public ProjectExternalPartyRelationshipController(CreateProjectExternalPartyRelationshipAction create, ProjectExternalPartyRelationshipQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create relationship")
    public ApiResponse<ProjectExternalPartyRelationshipResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateProjectExternalPartyRelationshipRequest r) {
        return ApiResponse.success(create.execute(new CreateProjectExternalPartyRelationshipCommand(projectId, r.organizationId(), r.relationshipType(), r.notes())));
    }
    @GetMapping @Operation(summary = "List relationships")
    public ApiResponse<List<ProjectExternalPartyRelationshipResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
}
