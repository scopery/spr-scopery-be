package com.company.scopery.modules.traceability.functionapi.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.functionapi.application.action.LinkFunctionApiAction;
import com.company.scopery.modules.traceability.functionapi.application.action.UnlinkFunctionApiAction;
import com.company.scopery.modules.traceability.functionapi.application.command.LinkFunctionApiCommand;
import com.company.scopery.modules.traceability.functionapi.application.response.FunctionApiResponse;
import com.company.scopery.modules.traceability.functionapi.application.service.FunctionApiQueryService;
import com.company.scopery.modules.traceability.functionapi.http.request.LinkFunctionApiRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.FUNCTION_APIS)
@Tag(name = "Traceability - Function APIs")
public class FunctionApiController {

    private final LinkFunctionApiAction link;
    private final UnlinkFunctionApiAction unlink;
    private final FunctionApiQueryService query;

    public FunctionApiController(LinkFunctionApiAction link,
                                 UnlinkFunctionApiAction unlink,
                                 FunctionApiQueryService query) {
        this.link = link;
        this.unlink = unlink;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Link an API endpoint to a function")
    public ApiResponse<FunctionApiResponse> link(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody LinkFunctionApiRequest r) {
        return ApiResponse.success(link.execute(
                new LinkFunctionApiCommand(projectId, functionalItemId, r.apiEndpointId(), r.note())));
    }

    @GetMapping
    @Operation(summary = "List API endpoints linked to a function")
    public ApiResponse<List<FunctionApiResponse>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId) {
        return ApiResponse.success(query.listByFunction(functionalItemId));
    }

    @DeleteMapping("/{apiEndpointId}")
    @Operation(summary = "Unlink an API endpoint from a function")
    public ApiResponse<Void> unlink(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID apiEndpointId) {
        unlink.execute(projectId, functionalItemId, apiEndpointId);
        return ApiResponse.success(null);
    }
}
