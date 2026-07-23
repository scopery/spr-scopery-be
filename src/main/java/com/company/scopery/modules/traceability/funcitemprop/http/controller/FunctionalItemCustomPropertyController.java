package com.company.scopery.modules.traceability.funcitemprop.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.funcitemprop.application.action.CreateFunctionalItemCustomPropertyAction;
import com.company.scopery.modules.traceability.funcitemprop.application.action.DeleteFunctionalItemCustomPropertyAction;
import com.company.scopery.modules.traceability.funcitemprop.application.action.UpdateFunctionalItemCustomPropertyAction;
import com.company.scopery.modules.traceability.funcitemprop.application.command.CreateFunctionalItemCustomPropertyCommand;
import com.company.scopery.modules.traceability.funcitemprop.application.command.UpdateFunctionalItemCustomPropertyCommand;
import com.company.scopery.modules.traceability.funcitemprop.application.response.FunctionalItemCustomPropertyResponse;
import com.company.scopery.modules.traceability.funcitemprop.application.service.FunctionalItemCustomPropertyQueryService;
import com.company.scopery.modules.traceability.funcitemprop.http.request.CreateFunctionalItemCustomPropertyRequest;
import com.company.scopery.modules.traceability.funcitemprop.http.request.UpdateFunctionalItemCustomPropertyRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.FUNCTIONAL_ITEM_CUSTOM_PROPS)
@Tag(name = "Traceability - Functional Item Properties")
public class FunctionalItemCustomPropertyController {

    private final CreateFunctionalItemCustomPropertyAction create;
    private final UpdateFunctionalItemCustomPropertyAction update;
    private final DeleteFunctionalItemCustomPropertyAction delete;
    private final FunctionalItemCustomPropertyQueryService query;

    public FunctionalItemCustomPropertyController(
            CreateFunctionalItemCustomPropertyAction create,
            UpdateFunctionalItemCustomPropertyAction update,
            DeleteFunctionalItemCustomPropertyAction delete,
            FunctionalItemCustomPropertyQueryService query
    ) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Add custom property to functional item")
    public ApiResponse<FunctionalItemCustomPropertyResponse> create(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody CreateFunctionalItemCustomPropertyRequest r
    ) {
        return ApiResponse.success(create.execute(new CreateFunctionalItemCustomPropertyCommand(
                functionalItemId, projectId, r.propKey(), r.propValue(), r.fieldType())));
    }

    @GetMapping
    @Operation(summary = "List custom properties of functional item")
    public ApiResponse<List<FunctionalItemCustomPropertyResponse>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId
    ) {
        return ApiResponse.success(query.list(functionalItemId, projectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get custom property by id")
    public ApiResponse<FunctionalItemCustomPropertyResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID id
    ) {
        return ApiResponse.success(query.get(id, functionalItemId, projectId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update custom property value and field type")
    public ApiResponse<FunctionalItemCustomPropertyResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFunctionalItemCustomPropertyRequest r
    ) {
        return ApiResponse.success(update.execute(new UpdateFunctionalItemCustomPropertyCommand(
                id, functionalItemId, projectId, r.propValue(), r.fieldType())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete custom property")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID id
    ) {
        delete.execute(id, functionalItemId, projectId);
        return ApiResponse.success(null);
    }
}
