package com.company.scopery.modules.collaboration.commentthread.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.commentthread.application.action.*;
import com.company.scopery.modules.collaboration.commentthread.application.command.*;
import com.company.scopery.modules.collaboration.commentthread.application.response.CommentThreadResponse;
import com.company.scopery.modules.collaboration.commentthread.application.service.CommentThreadQueryService;
import com.company.scopery.modules.collaboration.commentthread.http.request.CreateCommentThreadRequest;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.COMMENT_THREADS) @Tag(name = "Collaboration - Comment Threads")
public class CommentThreadController {
    private final CreateCommentThreadAction create; private final ResolveCommentThreadAction resolve;
    private final ArchiveCommentThreadAction archive; private final CommentThreadQueryService query;
    public CommentThreadController(CreateCommentThreadAction create, ResolveCommentThreadAction resolve, ArchiveCommentThreadAction archive, CommentThreadQueryService query) {
        this.create=create; this.resolve=resolve; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary="Create comment thread")
    public ApiResponse<CommentThreadResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateCommentThreadRequest r) {
        return ApiResponse.success(create.execute(new CreateCommentThreadCommand(projectId, r.targetType(), r.targetId(), r.title(), r.clientVisible())));
    }
    @GetMapping @Operation(summary="List comment threads")
    public ApiResponse<List<CommentThreadResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{threadId}") @Operation(summary="Get comment thread")
    public ApiResponse<CommentThreadResponse> get(@PathVariable UUID projectId, @PathVariable UUID threadId) { return ApiResponse.success(query.get(projectId, threadId)); }
    @PostMapping("/{threadId}/resolve") @Operation(summary="Resolve comment thread")
    public ApiResponse<CommentThreadResponse> resolve(@PathVariable UUID projectId, @PathVariable UUID threadId) { return ApiResponse.success(resolve.execute(new ResolveCommentThreadCommand(projectId, threadId))); }
    @PatchMapping("/{threadId}/archive") @Operation(summary="Archive comment thread")
    public ApiResponse<CommentThreadResponse> archive(@PathVariable UUID projectId, @PathVariable UUID threadId) { return ApiResponse.success(archive.execute(new ArchiveCommentThreadCommand(projectId, threadId))); }
}
