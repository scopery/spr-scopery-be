package com.company.scopery.modules.servicesupport.comment.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.comment.application.action.AddSupportCommentAction;
import com.company.scopery.modules.servicesupport.comment.application.command.AddSupportCommentCommand;
import com.company.scopery.modules.servicesupport.comment.application.response.SupportCommentResponse;
import com.company.scopery.modules.servicesupport.comment.application.service.SupportCommentQueryService;
import com.company.scopery.modules.servicesupport.comment.http.request.AddSupportCommentRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Comments")
public class SupportCommentController {
    private final SupportCommentQueryService query;
    private final AddSupportCommentAction addAction;

    public SupportCommentController(SupportCommentQueryService query, AddSupportCommentAction addAction) {
        this.query = query; this.addAction = addAction;
    }

    @GetMapping(SupportApiPaths.CASE_COMMENTS)
    @Operation(summary = "List comments for a case")
    public ApiResponse<List<SupportCommentResponse>> list(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId,
            @RequestParam(defaultValue = "false") boolean portalView) {
        return ApiResponse.success(query.listByCase(workspaceId, caseId, portalView));
    }

    @PostMapping(SupportApiPaths.CASE_COMMENTS)
    @Operation(summary = "Add comment to a case")
    public ApiResponse<SupportCommentResponse> add(@PathVariable UUID workspaceId,
            @PathVariable UUID caseId, @RequestBody @Valid AddSupportCommentRequest req) {
        return ApiResponse.success(addAction.execute(workspaceId, caseId,
                new AddSupportCommentCommand(req.body(), req.visibility(), req.authorUserId())));
    }
}
