package com.company.scopery.modules.traceability.functionalitem.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.common.response.BulkDeleteResponse;
import com.company.scopery.modules.traceability.businessrule.application.command.CreateBusinessRuleCommand;
import com.company.scopery.modules.traceability.businessrule.http.request.CreateBusinessRuleRequest;
import com.company.scopery.modules.traceability.functionalitem.application.action.BulkCreateFunctionalItemJobHandler;
import com.company.scopery.modules.traceability.functionalitem.application.action.BulkDeleteFunctionalItemAction;
import com.company.scopery.modules.traceability.functionalitem.application.action.CreateFunctionalItemAction;
import com.company.scopery.modules.traceability.functionalitem.application.action.DeleteFunctionalItemAction;
import com.company.scopery.modules.traceability.functionalitem.application.action.ExecuteFunctionalItemsImportAction;
import com.company.scopery.modules.traceability.functionalitem.application.action.PreviewFunctionalItemsImportAction;
import com.company.scopery.modules.traceability.functionalitem.application.action.UpdateFunctionalItemAction;
import com.company.scopery.modules.traceability.functionalitem.application.command.BulkCreateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.command.BulkDeleteFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.command.CreateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.command.ExecuteFunctionalItemsImportCommand;
import com.company.scopery.modules.traceability.functionalitem.application.command.PreviewFunctionalItemsImportCommand;
import com.company.scopery.modules.traceability.functionalitem.application.command.UpdateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemImportPreviewResponse;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemImportResultResponse;
import com.company.scopery.modules.traceability.functionalitem.application.response.FunctionalItemResponse;
import com.company.scopery.modules.traceability.functionalitem.application.service.FunctionalItemQueryService;
import com.company.scopery.modules.traceability.functionalitem.http.request.BulkCreateFunctionalItemRequest;
import com.company.scopery.modules.traceability.functionalitem.http.request.BulkDeleteFunctionalItemRequest;
import com.company.scopery.modules.traceability.functionalitem.http.request.CreateFunctionalItemRequest;
import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemsExecuteRequest;
import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemsPreviewRequest;
import com.company.scopery.modules.traceability.functionalitem.http.request.UpdateFunctionalItemRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.platform.bulkjob.BulkJobResponse;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.FUNCTIONAL_ITEMS)
@Tag(name = "Traceability - Functional Items")
public class FunctionalItemController {

    private final CreateFunctionalItemAction createAction;
    private final UpdateFunctionalItemAction updateAction;
    private final DeleteFunctionalItemAction deleteAction;
    private final BulkDeleteFunctionalItemAction bulkDeleteAction;
    private final PreviewFunctionalItemsImportAction previewImportAction;
    private final ExecuteFunctionalItemsImportAction executeImportAction;
    private final FunctionalItemQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public FunctionalItemController(CreateFunctionalItemAction createAction,
                                    UpdateFunctionalItemAction updateAction,
                                    DeleteFunctionalItemAction deleteAction,
                                    BulkDeleteFunctionalItemAction bulkDeleteAction,
                                    PreviewFunctionalItemsImportAction previewImportAction,
                                    ExecuteFunctionalItemsImportAction executeImportAction,
                                    FunctionalItemQueryService query,
                                    BulkJobService bulkJobService,
                                    ObjectMapper objectMapper) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.bulkDeleteAction = bulkDeleteAction;
        this.previewImportAction = previewImportAction;
        this.executeImportAction = executeImportAction;
        this.query = query;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Operation(summary = "Create a functional item")
    public ApiResponse<FunctionalItemResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateFunctionalItemRequest r
    ) {
        return ApiResponse.success(createAction.execute(new CreateFunctionalItemCommand(
                projectId,
                r.workspaceId(),
                r.moduleId(),
                r.code(),
                r.title(),
                r.description(),
                r.priority(),
                r.type(),
                r.acceptanceCriteria(),
                toBusinessRuleCommands(r.businessRules())
        )));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create functional items (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(
            @PathVariable UUID projectId,
            @Valid @RequestBody BulkCreateFunctionalItemRequest r
    ) {
        var items = r.items().stream()
                .map(item -> new CreateFunctionalItemCommand(
                        projectId, item.workspaceId(), item.moduleId(),
                        item.code(), item.title(), item.description(),
                        item.priority(), item.type(), item.acceptanceCriteria(),
                        toBusinessRuleCommands(item.businessRules())))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateFunctionalItemCommand(projectId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateFunctionalItemJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @GetMapping
    @Operation(summary = "List / search functional items by project")
    public ApiResponse<List<FunctionalItemResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID moduleId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "50") int limit
    ) {
        if (q != null && !q.isBlank()) {
            return ApiResponse.success(query.search(projectId, q, limit));
        }
        return ApiResponse.success(moduleId != null
                ? query.listByModule(projectId, moduleId)
                : query.listByProject(projectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a functional item")
    public ApiResponse<FunctionalItemResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID id
    ) {
        return ApiResponse.success(query.get(id, projectId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a functional item")
    public ApiResponse<FunctionalItemResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFunctionalItemRequest r
    ) {
        return ApiResponse.success(updateAction.execute(new UpdateFunctionalItemCommand(
                id,
                projectId,
                r.moduleId(),
                r.title(),
                r.description(),
                r.priority(),
                r.status(),
                r.type(),
                r.acceptanceCriteria()
        )));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a functional item")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID id
    ) {
        deleteAction.execute(id, projectId);
        return ApiResponse.success(null);
    }

    @PostMapping("/bulk-delete")
    @Operation(summary = "Bulk delete functional items")
    public ApiResponse<BulkDeleteResponse> bulkDelete(
            @PathVariable UUID projectId,
            @Valid @RequestBody BulkDeleteFunctionalItemRequest r
    ) {
        return ApiResponse.success(bulkDeleteAction.execute(new BulkDeleteFunctionalItemCommand(projectId, r.ids())));
    }

    @PostMapping("/import/preview")
    @Operation(summary = "Preview functional items import — classifies each entry as toCreate, toUpdate, or conflict")
    public ApiResponse<FunctionalItemImportPreviewResponse> previewImport(
            @PathVariable UUID projectId,
            @Valid @RequestBody ImportFunctionalItemsPreviewRequest request
    ) {
        return ApiResponse.success(previewImportAction.execute(
                new PreviewFunctionalItemsImportCommand(projectId, request.items())));
    }

    @PostMapping("/import/execute")
    @Operation(summary = "Execute functional items import — bulk create + update in one transaction")
    public ApiResponse<FunctionalItemImportResultResponse> executeImport(
            @PathVariable UUID projectId,
            @RequestBody ImportFunctionalItemsExecuteRequest request
    ) {
        return ApiResponse.success(executeImportAction.execute(
                new ExecuteFunctionalItemsImportCommand(
                        projectId,
                        request.toCreate(),
                        request.toUpdate(),
                        request.archiveUnmatched())));
    }

    private List<CreateBusinessRuleCommand> toBusinessRuleCommands(List<CreateBusinessRuleRequest> rules) {
        if (rules == null || rules.isEmpty()) return List.of();
        return rules.stream()
                .map(r -> new CreateBusinessRuleCommand(null, null, r.code(), r.title(), r.description(), r.severity()))
                .toList();
    }
}
