package com.company.scopery.modules.traceability.overallstructure.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.overallstructure.application.response.CandidatesResponse;
import com.company.scopery.modules.traceability.overallstructure.application.response.OverallStructureResponse;
import com.company.scopery.modules.traceability.overallstructure.application.service.OverallStructureQueryService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.OVERALL_STRUCTURE)
@Tag(name = "Traceability - Overall Structure")
public class OverallStructureController {

    private final OverallStructureQueryService query;

    public OverallStructureController(OverallStructureQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "Get overall structure tree for an application")
    public ApiResponse<OverallStructureResponse> getStructure(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID applicationId) {
        return ApiResponse.success(query.getStructure(applicationId));
    }

    @GetMapping("/candidates")
    @Operation(summary = "Get all linkable candidates (screens, API endpoints, components) for an application")
    public ApiResponse<CandidatesResponse> getCandidates(@PathVariable UUID workspaceId,
                                                          @PathVariable UUID applicationId) {
        return ApiResponse.success(query.getCandidates(applicationId));
    }
}
