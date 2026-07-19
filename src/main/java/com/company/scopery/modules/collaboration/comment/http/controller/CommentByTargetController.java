package com.company.scopery.modules.collaboration.comment.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.collaboration.commentthread.application.response.CommentThreadResponse;
import com.company.scopery.modules.collaboration.commentthread.application.service.CommentThreadQueryService;
import com.company.scopery.modules.collaboration.shared.constant.CollaborationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(CollaborationApiPaths.COMMENTS) @Tag(name = "Collaboration - Comments")
public class CommentByTargetController {
    private final CommentThreadQueryService query;
    public CommentByTargetController(CommentThreadQueryService query) { this.query=query; }
    @GetMapping("/by-target") @Operation(summary="List threads by target")
    public ApiResponse<List<CommentThreadResponse>> byTarget(@PathVariable UUID projectId, @RequestParam String targetType, @RequestParam UUID targetId) {
        return ApiResponse.success(query.byTarget(projectId, targetType, targetId));
    }
}
