package com.company.scopery.modules.collaboration.artifactlink.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.artifactlink.application.action.*;
import com.company.scopery.modules.collaboration.artifactlink.application.command.*;
import com.company.scopery.modules.collaboration.artifactlink.application.response.MeetingArtifactLinkResponse;
import com.company.scopery.modules.collaboration.artifactlink.application.service.MeetingArtifactLinkQueryService;
import com.company.scopery.modules.collaboration.artifactlink.http.request.CreateArtifactLinkRequest;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.MEETINGS + "/{meetingId}/artifact-links") @Tag(name = "Collaboration - Artifact Links")
public class MeetingArtifactLinkController {
    private final CreateArtifactLinkAction create; private final RemoveArtifactLinkAction remove; private final MeetingArtifactLinkQueryService query;
    public MeetingArtifactLinkController(CreateArtifactLinkAction create, RemoveArtifactLinkAction remove, MeetingArtifactLinkQueryService query) {
        this.create=create; this.remove=remove; this.query=query;
    }
    @PostMapping @Operation(summary="Create artifact link")
    public ApiResponse<MeetingArtifactLinkResponse> create(@PathVariable UUID projectId, @PathVariable UUID meetingId, @Valid @RequestBody CreateArtifactLinkRequest r) {
        return ApiResponse.success(create.execute(new CreateArtifactLinkCommand(projectId, meetingId, r.agendaItemId(), r.noteId(), r.actionItemId(), r.targetType(), r.targetId(), r.linkType())));
    }
    @GetMapping @Operation(summary="List artifact links")
    public ApiResponse<List<MeetingArtifactLinkResponse>> list(@PathVariable UUID projectId, @PathVariable UUID meetingId) { return ApiResponse.success(query.list(projectId, meetingId)); }
    @DeleteMapping("/{linkId}") @Operation(summary="Remove artifact link")
    public ApiResponse<Void> remove(@PathVariable UUID projectId, @PathVariable UUID meetingId, @PathVariable UUID linkId) {
        remove.execute(new RemoveArtifactLinkCommand(projectId, meetingId, linkId)); return ApiResponse.success(null);
    }
}
