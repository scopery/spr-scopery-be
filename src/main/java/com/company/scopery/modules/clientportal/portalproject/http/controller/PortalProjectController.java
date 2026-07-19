package com.company.scopery.modules.clientportal.portalproject.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.clientportal.portalproject.application.response.PortalProjectSummaryResponse;
import com.company.scopery.modules.clientportal.portalproject.application.service.PortalProjectQueryService;
import com.company.scopery.modules.clientportal.review.application.response.ClientReviewRequestResponse;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(ClientPortalApiPaths.PORTAL_PROJECTS)
@Tag(name = "Client Portal - Projects")
public class PortalProjectController {
    private final PortalProjectQueryService query;
    public PortalProjectController(PortalProjectQueryService query) { this.query = query; }
    @GetMapping @Operation(summary = "List projects granted to current portal account")
    public ApiResponse<List<PortalProjectSummaryResponse>> list() { return ApiResponse.success(query.listMyProjects()); }
    @GetMapping("/{projectId}/reviews") @Operation(summary = "List review requests for granted project")
    public ApiResponse<List<ClientReviewRequestResponse>> reviews(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listReviews(projectId));
    }
}
