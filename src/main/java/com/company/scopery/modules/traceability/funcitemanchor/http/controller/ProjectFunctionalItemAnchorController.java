package com.company.scopery.modules.traceability.funcitemanchor.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.funcitemanchor.application.response.FunctionalItemAnchorResponse;
import com.company.scopery.modules.traceability.funcitemanchor.application.service.FunctionalItemAnchorQueryService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.PROJECT_FUNCTIONAL_ITEM_ANCHORS)
@Tag(name = "Traceability - Functional Item Anchors")
public class ProjectFunctionalItemAnchorController {

    private final FunctionalItemAnchorQueryService query;

    public ProjectFunctionalItemAnchorController(FunctionalItemAnchorQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "List all FR anchors for a project, optionally filtered by node")
    public ApiResponse<List<FunctionalItemAnchorResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) UUID nodeId
    ) {
        if (nodeType != null && nodeId != null) {
            return ApiResponse.success(query.listByNode(projectId, nodeType, nodeId));
        }
        return ApiResponse.success(query.listByProject(projectId));
    }
}
