package com.company.scopery.modules.traceability.screeneventitem.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screeneventitem.application.action.CreateRegistryScreenEventItemAction;
import com.company.scopery.modules.traceability.screeneventitem.application.action.DeleteRegistryScreenEventItemAction;
import com.company.scopery.modules.traceability.screeneventitem.application.action.UpdateRegistryScreenEventItemAction;
import com.company.scopery.modules.traceability.screeneventitem.application.command.CreateRegistryScreenEventItemCommand;
import com.company.scopery.modules.traceability.screeneventitem.application.command.UpdateRegistryScreenEventItemCommand;
import com.company.scopery.modules.traceability.screeneventitem.application.response.RegistryScreenEventItemResponse;
import com.company.scopery.modules.traceability.screeneventitem.application.service.RegistryScreenEventItemQueryService;
import com.company.scopery.modules.traceability.screeneventitem.http.request.CreateRegistryScreenEventItemRequest;
import com.company.scopery.modules.traceability.screeneventitem.http.request.UpdateRegistryScreenEventItemRequest;
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
@RequestMapping(TraceabilityApiPaths.SCREEN_EVENT_ITEMS)
@Tag(name = "Traceability - Screen Event Items")
public class RegistryScreenEventItemController {

    private final CreateRegistryScreenEventItemAction create;
    private final UpdateRegistryScreenEventItemAction update;
    private final DeleteRegistryScreenEventItemAction delete;
    private final RegistryScreenEventItemQueryService query;

    public RegistryScreenEventItemController(CreateRegistryScreenEventItemAction create,
                                             UpdateRegistryScreenEventItemAction update,
                                             DeleteRegistryScreenEventItemAction delete,
                                             RegistryScreenEventItemQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create screen event item")
    public ApiResponse<RegistryScreenEventItemResponse> create(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID screenId,
                                                               @Valid @RequestBody CreateRegistryScreenEventItemRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenEventItemCommand(
                workspaceId, screenId, r.modeId(), r.triggerFieldId(), r.triggerActionCode(),
                r.title(), r.content(), r.conditionNote(), r.targetScreenId(), r.targetModeCode(),
                r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List screen event items")
    public ApiResponse<List<RegistryScreenEventItemResponse>> list(@PathVariable UUID workspaceId,
                                                                   @PathVariable UUID screenId) {
        return ApiResponse.success(query.list(workspaceId, screenId));
    }

    @GetMapping("/{eventItemId}")
    @Operation(summary = "Get screen event item")
    public ApiResponse<RegistryScreenEventItemResponse> get(@PathVariable UUID workspaceId,
                                                            @PathVariable UUID screenId,
                                                            @PathVariable UUID eventItemId) {
        return ApiResponse.success(query.get(workspaceId, screenId, eventItemId));
    }

    @PutMapping("/{eventItemId}")
    @Operation(summary = "Update screen event item")
    public ApiResponse<RegistryScreenEventItemResponse> update(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID screenId,
                                                               @PathVariable UUID eventItemId,
                                                               @Valid @RequestBody UpdateRegistryScreenEventItemRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryScreenEventItemCommand(
                workspaceId, screenId, eventItemId, r.modeId(), r.triggerFieldId(), r.triggerActionCode(),
                r.title(), r.content(), r.conditionNote(), r.targetScreenId(), r.targetModeCode(),
                r.displayOrder())));
    }

    @DeleteMapping("/{eventItemId}")
    @Operation(summary = "Delete screen event item")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                    @PathVariable UUID screenId,
                                    @PathVariable UUID eventItemId) {
        delete.execute(workspaceId, eventItemId);
        return ApiResponse.success(null);
    }
}
