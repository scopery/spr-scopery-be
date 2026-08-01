package com.company.scopery.modules.traceability.functioncomm.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.functioncomm.application.action.LinkFunctionCommunicationAction;
import com.company.scopery.modules.traceability.functioncomm.application.action.UnlinkFunctionCommunicationAction;
import com.company.scopery.modules.traceability.functioncomm.application.command.LinkFunctionCommunicationCommand;
import com.company.scopery.modules.traceability.functioncomm.application.response.FunctionCommunicationResponse;
import com.company.scopery.modules.traceability.functioncomm.application.service.FunctionCommunicationQueryService;
import com.company.scopery.modules.traceability.functioncomm.http.request.LinkFunctionCommunicationRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.FUNCTION_COMMUNICATIONS)
@Tag(name = "Traceability - Function Communications")
public class FunctionCommunicationController {

    private final LinkFunctionCommunicationAction link;
    private final UnlinkFunctionCommunicationAction unlink;
    private final FunctionCommunicationQueryService query;

    public FunctionCommunicationController(LinkFunctionCommunicationAction link,
                                           UnlinkFunctionCommunicationAction unlink,
                                           FunctionCommunicationQueryService query) {
        this.link = link;
        this.unlink = unlink;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Link a communication specification to a function")
    public ApiResponse<FunctionCommunicationResponse> link(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody LinkFunctionCommunicationRequest r) {
        return ApiResponse.success(link.execute(
                new LinkFunctionCommunicationCommand(projectId, functionalItemId, r.communicationId(), r.note())));
    }

    @GetMapping
    @Operation(summary = "List communication specifications linked to a function")
    public ApiResponse<List<FunctionCommunicationResponse>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId) {
        return ApiResponse.success(query.listByFunction(projectId, functionalItemId));
    }

    @DeleteMapping("/{communicationId}")
    @Operation(summary = "Unlink a communication specification from a function")
    public ApiResponse<Void> unlink(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID communicationId) {
        unlink.execute(projectId, functionalItemId, communicationId);
        return ApiResponse.success(null);
    }
}
