package com.company.scopery.modules.traceability.structurerelation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.structurerelation.application.action.AddStructureRelationAction;
import com.company.scopery.modules.traceability.structurerelation.application.action.RemoveStructureRelationAction;
import com.company.scopery.modules.traceability.structurerelation.application.command.AddStructureRelationCommand;
import com.company.scopery.modules.traceability.structurerelation.application.response.StructureRelationResponse;
import com.company.scopery.modules.traceability.structurerelation.application.service.StructureRelationQueryService;
import com.company.scopery.modules.traceability.structurerelation.http.request.AddStructureRelationRequest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Hidden
@RestController
@RequestMapping(TraceabilityApiPaths.STRUCTURE_RELATIONS)
@Tag(name = "Traceability - Structure Relations")
public class StructureRelationController {

    private final AddStructureRelationAction add;
    private final RemoveStructureRelationAction remove;
    private final StructureRelationQueryService query;

    public StructureRelationController(AddStructureRelationAction add,
                                       RemoveStructureRelationAction remove,
                                       StructureRelationQueryService query) {
        this.add = add;
        this.remove = remove;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Add a structure relation between two nodes")
    public ApiResponse<StructureRelationResponse> add(
            @PathVariable UUID workspaceId,
            @PathVariable UUID applicationId,
            @Valid @RequestBody AddStructureRelationRequest r) {
        return ApiResponse.success(add.execute(new AddStructureRelationCommand(
                applicationId, workspaceId,
                r.fromNodeType(), r.fromNodeId(),
                r.toNodeType(), r.toNodeId(),
                r.relationType())));
    }

    @GetMapping
    @Operation(summary = "List structure relations for an application, optionally filtered by node")
    public ApiResponse<List<StructureRelationResponse>> list(
            @PathVariable UUID applicationId,
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) UUID nodeId) {
        return ApiResponse.success(query.list(applicationId, nodeType, nodeId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a structure relation")
    public ApiResponse<Void> remove(
            @PathVariable UUID applicationId,
            @PathVariable UUID id) {
        remove.execute(id, applicationId);
        return ApiResponse.success(null);
    }
}
