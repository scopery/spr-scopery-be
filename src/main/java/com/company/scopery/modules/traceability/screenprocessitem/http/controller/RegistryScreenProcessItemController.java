package com.company.scopery.modules.traceability.screenprocessitem.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screenprocessitem.application.action.CreateRegistryScreenProcessItemAction;
import com.company.scopery.modules.traceability.screenprocessitem.application.action.DeleteRegistryScreenProcessItemAction;
import com.company.scopery.modules.traceability.screenprocessitem.application.action.UpdateRegistryScreenProcessItemAction;
import com.company.scopery.modules.traceability.screenprocessitem.application.command.CreateRegistryScreenProcessItemCommand;
import com.company.scopery.modules.traceability.screenprocessitem.application.command.UpdateRegistryScreenProcessItemCommand;
import com.company.scopery.modules.traceability.screenprocessitem.application.response.RegistryScreenProcessItemResponse;
import com.company.scopery.modules.traceability.screenprocessitem.application.service.RegistryScreenProcessItemQueryService;
import com.company.scopery.modules.traceability.screenprocessitem.http.request.CreateRegistryScreenProcessItemRequest;
import com.company.scopery.modules.traceability.screenprocessitem.http.request.UpdateRegistryScreenProcessItemRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_PROCESS_ITEMS)
@Tag(name = "Traceability - Screen Process Items")
public class RegistryScreenProcessItemController {

    private final CreateRegistryScreenProcessItemAction create;
    private final UpdateRegistryScreenProcessItemAction update;
    private final DeleteRegistryScreenProcessItemAction delete;
    private final RegistryScreenProcessItemQueryService query;

    public RegistryScreenProcessItemController(CreateRegistryScreenProcessItemAction create,
                                               UpdateRegistryScreenProcessItemAction update,
                                               DeleteRegistryScreenProcessItemAction delete,
                                               RegistryScreenProcessItemQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create screen process item")
    public ApiResponse<RegistryScreenProcessItemResponse> create(@PathVariable UUID workspaceId,
                                                                 @PathVariable UUID screenId,
                                                                 @Valid @RequestBody CreateRegistryScreenProcessItemRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenProcessItemCommand(
                workspaceId, screenId, r.modeId(), r.targetFieldId(),
                r.title(), r.content(), r.sourceTable(), r.conditionNote(), r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List screen process items")
    public ApiResponse<List<RegistryScreenProcessItemResponse>> list(@PathVariable UUID workspaceId,
                                                                     @PathVariable UUID screenId) {
        return ApiResponse.success(query.list(workspaceId, screenId));
    }

    @GetMapping("/{processItemId}")
    @Operation(summary = "Get screen process item")
    public ApiResponse<RegistryScreenProcessItemResponse> get(@PathVariable UUID workspaceId,
                                                              @PathVariable UUID screenId,
                                                              @PathVariable UUID processItemId) {
        return ApiResponse.success(query.get(workspaceId, screenId, processItemId));
    }

    @PutMapping("/{processItemId}")
    @Operation(summary = "Update screen process item")
    public ApiResponse<RegistryScreenProcessItemResponse> update(@PathVariable UUID workspaceId,
                                                                 @PathVariable UUID screenId,
                                                                 @PathVariable UUID processItemId,
                                                                 @Valid @RequestBody UpdateRegistryScreenProcessItemRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryScreenProcessItemCommand(
                workspaceId, screenId, processItemId, r.modeId(), r.targetFieldId(),
                r.title(), r.content(), r.sourceTable(), r.conditionNote(), r.displayOrder())));
    }

    @DeleteMapping("/{processItemId}")
    @Operation(summary = "Delete screen process item")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                    @PathVariable UUID screenId,
                                    @PathVariable UUID processItemId) {
        delete.execute(workspaceId, processItemId);
        return ApiResponse.success(null);
    }
}
