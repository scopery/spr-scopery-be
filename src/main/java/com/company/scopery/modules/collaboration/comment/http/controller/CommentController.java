package com.company.scopery.modules.collaboration.comment.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.comment.application.action.*;
import com.company.scopery.modules.collaboration.comment.application.command.*;
import com.company.scopery.modules.collaboration.comment.application.response.CommentResponse;
import com.company.scopery.modules.collaboration.comment.application.service.CommentQueryService;
import com.company.scopery.modules.collaboration.comment.http.request.*;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Collaboration - Comments")
public class CommentController {
    private final CreateCommentAction create; private final UpdateCommentAction update; private final DeleteCommentAction delete; private final CommentQueryService query;
    public CommentController(CreateCommentAction create, UpdateCommentAction update, DeleteCommentAction delete, CommentQueryService query) {
        this.create=create; this.update=update; this.delete=delete; this.query=query;
    }
    @PostMapping(CollaborationApiPaths.COMMENT_THREADS + "/{threadId}/comments") @Operation(summary="Create comment")
    public ApiResponse<CommentResponse> create(@PathVariable UUID projectId, @PathVariable UUID threadId, @Valid @RequestBody CreateCommentRequest r) {
        return ApiResponse.success(create.execute(new CreateCommentCommand(projectId, threadId, r.parentCommentId(), r.body(), r.clientVisible(), r.mentionUserIds())));
    }
    @GetMapping(CollaborationApiPaths.COMMENT_THREADS + "/{threadId}/comments") @Operation(summary="List comments")
    public ApiResponse<List<CommentResponse>> list(@PathVariable UUID projectId, @PathVariable UUID threadId) {
        return ApiResponse.success(query.listByThread(projectId, threadId));
    }
    @PutMapping(CollaborationApiPaths.COMMENTS + "/{commentId}") @Operation(summary="Update comment")
    public ApiResponse<CommentResponse> update(@PathVariable UUID projectId, @PathVariable UUID commentId, @Valid @RequestBody UpdateCommentRequest r) {
        return ApiResponse.success(update.execute(new UpdateCommentCommand(projectId, commentId, r.body())));
    }
    @PostMapping(CollaborationApiPaths.COMMENTS + "/{commentId}/delete") @Operation(summary="Soft delete comment")
    public ApiResponse<Void> delete(@PathVariable UUID projectId, @PathVariable UUID commentId) {
        delete.execute(new DeleteCommentCommand(projectId, commentId)); return ApiResponse.success(null);
    }
}
