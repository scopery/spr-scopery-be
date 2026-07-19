package com.company.scopery.modules.clientportal.comment.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.comment.application.response.ClientCommentResponse;
import com.company.scopery.modules.clientportal.comment.application.service.ClientCommentQueryService;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.COMMENTS)
@Tag(name = "Client Portal - Comments")
public class ClientCommentController {
    private final ClientCommentQueryService query;
    public ClientCommentController(ClientCommentQueryService query) { this.query = query; }
    @GetMapping @Operation(summary = "List comments (internal)")
    public ApiResponse<List<ClientCommentResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.listInternal(projectId)); }
}
