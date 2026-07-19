package com.company.scopery.modules.documenthub.comment.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.comment.application.action.*;
import com.company.scopery.modules.documenthub.comment.application.command.*;
import com.company.scopery.modules.documenthub.comment.application.response.CommentResponse;
import com.company.scopery.modules.documenthub.comment.application.response.CommentThreadResponse;
import com.company.scopery.modules.documenthub.comment.application.service.CommentThreadQueryService;
import com.company.scopery.modules.documenthub.comment.http.request.AddCommentRequest;
import com.company.scopery.modules.documenthub.comment.http.request.OpenCommentThreadRequest;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Document Hub - Comment Threads")
public class DocumentCommentController {

    private final OpenCommentThreadAction openThread;
    private final AddCommentAction addComment;
    private final ResolveCommentThreadAction resolveThread;
    private final DeleteCommentAction deleteComment;
    private final CommentThreadQueryService query;

    public DocumentCommentController(OpenCommentThreadAction openThread,
                                      AddCommentAction addComment,
                                      ResolveCommentThreadAction resolveThread,
                                      DeleteCommentAction deleteComment,
                                      CommentThreadQueryService query) {
        this.openThread = openThread;
        this.addComment = addComment;
        this.resolveThread = resolveThread;
        this.deleteComment = deleteComment;
        this.query = query;
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_COMMENT_THREADS)
    @Operation(summary = "List comment threads for a document")
    public ApiResponse<List<CommentThreadResponse>> list(@PathVariable UUID projectId,
                                                          @PathVariable UUID documentId) {
        return ApiResponse.success(query.listByDocument(projectId, documentId));
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_COMMENT_THREADS + "/{threadId}")
    @Operation(summary = "Get a comment thread with its comments")
    public ApiResponse<CommentThreadResponse> get(@PathVariable UUID projectId,
                                                   @PathVariable UUID documentId,
                                                   @PathVariable UUID threadId) {
        return ApiResponse.success(query.get(projectId, documentId, threadId));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_COMMENT_THREADS)
    @Operation(summary = "Open a new comment thread with first comment")
    public ApiResponse<CommentThreadResponse> open(@PathVariable UUID projectId,
                                                    @PathVariable UUID documentId,
                                                    @Valid @RequestBody OpenCommentThreadRequest r) {
        return ApiResponse.success(openThread.execute(new OpenCommentThreadCommand(
                projectId, documentId, r.blockId(), r.anchorText(), r.firstCommentBody())));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_COMMENT_THREADS + "/{threadId}/comments")
    @Operation(summary = "Add a comment to a thread")
    public ApiResponse<CommentResponse> addComment(@PathVariable UUID projectId,
                                                    @PathVariable UUID documentId,
                                                    @PathVariable UUID threadId,
                                                    @Valid @RequestBody AddCommentRequest r) {
        return ApiResponse.success(addComment.execute(new AddCommentCommand(projectId, documentId, threadId, r.body())));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_COMMENT_THREADS + "/{threadId}/resolve")
    @Operation(summary = "Resolve a comment thread")
    public ApiResponse<CommentThreadResponse> resolve(@PathVariable UUID projectId,
                                                       @PathVariable UUID documentId,
                                                       @PathVariable UUID threadId) {
        return ApiResponse.success(resolveThread.execute(new ResolveCommentThreadCommand(projectId, documentId, threadId)));
    }

    @DeleteMapping(DocumentHubApiPaths.DOCUMENT_COMMENT_THREADS + "/{threadId}/comments/{commentId}")
    @Operation(summary = "Soft-delete a comment")
    public ApiResponse<CommentResponse> deleteComment(@PathVariable UUID projectId,
                                                       @PathVariable UUID documentId,
                                                       @PathVariable UUID threadId,
                                                       @PathVariable UUID commentId) {
        return ApiResponse.success(deleteComment.execute(new DeleteCommentCommand(projectId, documentId, commentId)));
    }
}
