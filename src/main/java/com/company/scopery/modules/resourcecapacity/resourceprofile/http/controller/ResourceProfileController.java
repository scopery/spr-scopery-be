package com.company.scopery.modules.resourcecapacity.resourceprofile.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.action.*;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.command.CreateResourceProfileCommand;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.response.ResourceProfileResponse;
import com.company.scopery.modules.resourcecapacity.resourceprofile.application.service.ResourceProfileQueryService;
import com.company.scopery.modules.resourcecapacity.resourceprofile.http.request.CreateResourceProfileRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Resource Capacity - Profiles")
public class ResourceProfileController {
    private final CreateResourceProfileAction create; private final ArchiveResourceProfileAction archive;
    private final SyncResourcesFromMembersAction sync; private final ResourceProfileQueryService query;
    public ResourceProfileController(CreateResourceProfileAction create, ArchiveResourceProfileAction archive,
                                     SyncResourcesFromMembersAction sync, ResourceProfileQueryService query) {
        this.create=create; this.archive=archive; this.sync=sync; this.query=query;
    }
    @PostMapping(CapacityApiPaths.RESOURCES) @Operation(summary="Create resource profile")
    public ApiResponse<ResourceProfileResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateResourceProfileRequest r) {
        return ApiResponse.success(create.execute(new CreateResourceProfileCommand(workspaceId, r.resourceType(), r.displayName(),
                r.linkedUserId(), r.linkedWorkspaceMemberId(), r.primaryRoleId())));
    }
    @GetMapping(CapacityApiPaths.RESOURCES) @Operation(summary="List resource profiles")
    public ApiResponse<List<ResourceProfileResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @GetMapping(CapacityApiPaths.RESOURCE_BY_ID) @Operation(summary="Get resource profile")
    public ApiResponse<ResourceProfileResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID resourceId) {
        return ApiResponse.success(query.get(workspaceId, resourceId));
    }
    @PatchMapping(CapacityApiPaths.RESOURCE_ARCHIVE) @Operation(summary="Archive resource profile")
    public ApiResponse<ResourceProfileResponse> archive(@PathVariable UUID workspaceId, @PathVariable UUID resourceId) {
        return ApiResponse.success(archive.execute(workspaceId, resourceId, null));
    }
    @PostMapping(CapacityApiPaths.RESOURCE_SYNC) @Operation(summary="Sync resource profiles from workspace members")
    public ApiResponse<List<ResourceProfileResponse>> sync(@PathVariable UUID workspaceId) { return ApiResponse.success(sync.execute(workspaceId)); }
}
