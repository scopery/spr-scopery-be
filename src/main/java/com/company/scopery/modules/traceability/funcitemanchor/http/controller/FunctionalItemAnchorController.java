package com.company.scopery.modules.traceability.funcitemanchor.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.funcitemanchor.application.action.AddFunctionalItemAnchorAction;
import com.company.scopery.modules.traceability.funcitemanchor.application.action.RemoveFunctionalItemAnchorAction;
import com.company.scopery.modules.traceability.funcitemanchor.application.command.AddFunctionalItemAnchorCommand;
import com.company.scopery.modules.traceability.funcitemanchor.application.response.FunctionalItemAnchorResponse;
import com.company.scopery.modules.traceability.funcitemanchor.application.service.FunctionalItemAnchorQueryService;
import com.company.scopery.modules.traceability.funcitemanchor.http.request.AddFunctionalItemAnchorRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.FUNCTIONAL_ITEM_ANCHORS)
@Tag(name = "Traceability - Functional Item Anchors")
public class FunctionalItemAnchorController {

    private final AddFunctionalItemAnchorAction add;
    private final RemoveFunctionalItemAnchorAction remove;
    private final FunctionalItemAnchorQueryService query;

    public FunctionalItemAnchorController(
            AddFunctionalItemAnchorAction add,
            RemoveFunctionalItemAnchorAction remove,
            FunctionalItemAnchorQueryService query
    ) {
        this.add = add;
        this.remove = remove;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Add anchor to functional item")
    public ApiResponse<FunctionalItemAnchorResponse> addAnchor(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody AddFunctionalItemAnchorRequest r
    ) {
        return ApiResponse.success(add.execute(new AddFunctionalItemAnchorCommand(
                functionalItemId, projectId, r.nodeType(), r.nodeId(), r.note())));
    }

    @GetMapping
    @Operation(summary = "List anchors of functional item")
    public ApiResponse<List<FunctionalItemAnchorResponse>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId
    ) {
        return ApiResponse.success(query.list(functionalItemId, projectId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove anchor from functional item")
    public ApiResponse<Void> remove(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID id
    ) {
        remove.execute(id, functionalItemId, projectId);
        return ApiResponse.success(null);
    }
}
