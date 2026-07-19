package com.company.scopery.modules.configuration.tag.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import com.company.scopery.modules.configuration.tag.application.action.*;
import com.company.scopery.modules.configuration.tag.application.command.*;
import com.company.scopery.modules.configuration.tag.application.response.TagAssignmentResponse;
import com.company.scopery.modules.configuration.tag.application.service.TagAssignmentQueryService;
import com.company.scopery.modules.configuration.tag.http.request.CreateTagAssignmentRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.TAG_ASSIGNMENTS) @Tag(name = "Configuration - Tag Assignments")
public class TagAssignmentController {
    private final AssignTagAction assign; private final RemoveTagAssignmentAction remove; private final TagAssignmentQueryService query;
    public TagAssignmentController(AssignTagAction assign, RemoveTagAssignmentAction remove, TagAssignmentQueryService query) {
        this.assign=assign; this.remove=remove; this.query=query;
    }
    @PostMapping @Operation(summary="Assign tag")
    public ApiResponse<TagAssignmentResponse> assign(@PathVariable UUID workspaceId, @Valid @RequestBody CreateTagAssignmentRequest r) {
        return ApiResponse.success(assign.execute(new AssignTagCommand(workspaceId, r.tagDefinitionId(), r.objectTypeCode(), r.targetId())));
    }
    @GetMapping @Operation(summary="List tag assignments")
    public ApiResponse<List<TagAssignmentResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @DeleteMapping("/{assignmentId}") @Operation(summary="Remove tag assignment")
    public ApiResponse<Void> remove(@PathVariable UUID workspaceId, @PathVariable UUID assignmentId) {
        remove.execute(new RemoveTagAssignmentCommand(workspaceId, assignmentId)); return ApiResponse.success(null);
    }
}
