package com.company.scopery.modules.projectbaseline.changerequest.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.projectbaseline.changeimpact.application.action.CalculateChangeImpactAction;
import com.company.scopery.modules.projectbaseline.changeimpact.application.action.UpsertChangeImpactAction;
import com.company.scopery.modules.projectbaseline.changeimpact.application.response.ChangeImpactResponse;
import com.company.scopery.modules.projectbaseline.changeimpact.application.service.ChangeImpactQueryService;
import com.company.scopery.modules.projectbaseline.changeimpact.http.request.UpdateChangeImpactRequest;
import com.company.scopery.modules.projectbaseline.changeorder.application.action.*;
import com.company.scopery.modules.projectbaseline.changeorder.application.response.ChangeOrderResponse;
import com.company.scopery.modules.projectbaseline.changeorder.application.service.ChangeOrderQueryService;
import com.company.scopery.modules.projectbaseline.changeorder.http.request.*;
import com.company.scopery.modules.projectbaseline.changerequest.application.action.*;
import com.company.scopery.modules.projectbaseline.changerequest.application.command.CreateChangeRequestCommand;
import com.company.scopery.modules.projectbaseline.changerequest.application.command.UpdateChangeRequestCommand;
import com.company.scopery.modules.projectbaseline.changerequest.application.response.ChangeRequestResponse;
import com.company.scopery.modules.projectbaseline.changerequest.application.service.ChangeRequestQueryService;
import com.company.scopery.modules.projectbaseline.changerequest.http.request.*;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.action.*;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.command.CreateChangeRequestItemCommand;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.command.UpdateChangeRequestItemCommand;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.response.ChangeRequestItemResponse;
import com.company.scopery.modules.projectbaseline.changerequestitem.application.service.ChangeRequestItemQueryService;
import com.company.scopery.modules.projectbaseline.changerequestitem.http.request.*;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Change Request")
public class ChangeRequestController {

    private final ChangeRequestQueryService queryService;
    private final CreateChangeRequestAction createAction;
    private final UpdateChangeRequestAction updateAction;
    private final SubmitChangeRequestAction submitAction;
    private final ApproveChangeRequestAction approveAction;
    private final RejectChangeRequestAction rejectAction;
    private final CancelChangeRequestAction cancelAction;
    private final ApplyChangeRequestAction applyAction;
    private final ArchiveChangeRequestAction archiveAction;
    private final ChangeRequestItemQueryService itemQueryService;
    private final CreateChangeRequestItemAction createItemAction;
    private final UpdateChangeRequestItemAction updateItemAction;
    private final DeleteChangeRequestItemAction deleteItemAction;
    private final ChangeImpactQueryService impactQueryService;
    private final UpsertChangeImpactAction upsertImpactAction;
    private final CalculateChangeImpactAction calculateImpactAction;
    private final ChangeOrderQueryService changeOrderQueryService;
    private final CreateChangeOrderAction createChangeOrderAction;
    private final UpdateChangeOrderAction updateChangeOrderAction;
    private final ApproveChangeOrderAction approveChangeOrderAction;
    private final RejectChangeOrderAction rejectChangeOrderAction;
    private final ArchiveChangeOrderAction archiveChangeOrderAction;

    public ChangeRequestController(
            ChangeRequestQueryService queryService, CreateChangeRequestAction createAction,
            UpdateChangeRequestAction updateAction, SubmitChangeRequestAction submitAction,
            ApproveChangeRequestAction approveAction, RejectChangeRequestAction rejectAction,
            CancelChangeRequestAction cancelAction, ApplyChangeRequestAction applyAction,
            ArchiveChangeRequestAction archiveAction, ChangeRequestItemQueryService itemQueryService,
            CreateChangeRequestItemAction createItemAction, UpdateChangeRequestItemAction updateItemAction,
            DeleteChangeRequestItemAction deleteItemAction, ChangeImpactQueryService impactQueryService,
            UpsertChangeImpactAction upsertImpactAction, CalculateChangeImpactAction calculateImpactAction,
            ChangeOrderQueryService changeOrderQueryService, CreateChangeOrderAction createChangeOrderAction,
            UpdateChangeOrderAction updateChangeOrderAction, ApproveChangeOrderAction approveChangeOrderAction,
            RejectChangeOrderAction rejectChangeOrderAction, ArchiveChangeOrderAction archiveChangeOrderAction) {
        this.queryService = queryService; this.createAction = createAction; this.updateAction = updateAction;
        this.submitAction = submitAction; this.approveAction = approveAction; this.rejectAction = rejectAction;
        this.cancelAction = cancelAction; this.applyAction = applyAction; this.archiveAction = archiveAction;
        this.itemQueryService = itemQueryService; this.createItemAction = createItemAction;
        this.updateItemAction = updateItemAction; this.deleteItemAction = deleteItemAction;
        this.impactQueryService = impactQueryService; this.upsertImpactAction = upsertImpactAction;
        this.calculateImpactAction = calculateImpactAction; this.changeOrderQueryService = changeOrderQueryService;
        this.createChangeOrderAction = createChangeOrderAction; this.updateChangeOrderAction = updateChangeOrderAction;
        this.approveChangeOrderAction = approveChangeOrderAction; this.rejectChangeOrderAction = rejectChangeOrderAction;
        this.archiveChangeOrderAction = archiveChangeOrderAction;
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create change request")
    public ApiResponse<ChangeRequestResponse> create(@PathVariable UUID projectId,
                                                     @Valid @RequestBody CreateChangeRequestRequest request) {
        return ApiResponse.success(createAction.execute(new CreateChangeRequestCommand(
                projectId, request.code(), request.title(), request.description(), request.changeType(),
                request.priority(), request.baselineId(), request.reason())));
    }

    @GetMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS)
    public ApiResponse<List<ChangeRequestResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.list(projectId));
    }

    @GetMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}")
    public ApiResponse<ChangeRequestResponse> get(@PathVariable UUID projectId, @PathVariable UUID changeRequestId) {
        return ApiResponse.success(queryService.get(projectId, changeRequestId));
    }

    @PutMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}")
    public ApiResponse<ChangeRequestResponse> update(@PathVariable UUID projectId, @PathVariable UUID changeRequestId,
                                                     @Valid @RequestBody UpdateChangeRequestRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateChangeRequestCommand(
                projectId, changeRequestId, request.title(), request.description(), request.changeType(),
                request.priority(), request.baselineId(), request.reason())));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}/submit")
    public ApiResponse<ChangeRequestResponse> submit(@PathVariable UUID projectId, @PathVariable UUID changeRequestId) {
        return ApiResponse.success(submitAction.execute(projectId, changeRequestId));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}/approve")
    public ApiResponse<ChangeRequestResponse> approve(@PathVariable UUID projectId, @PathVariable UUID changeRequestId) {
        return ApiResponse.success(approveAction.execute(projectId, changeRequestId));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}/reject")
    public ApiResponse<ChangeRequestResponse> reject(@PathVariable UUID projectId, @PathVariable UUID changeRequestId,
                                                     @Valid @RequestBody RejectChangeRequestRequest request) {
        return ApiResponse.success(rejectAction.execute(projectId, changeRequestId, request.reason()));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}/cancel")
    public ApiResponse<ChangeRequestResponse> cancel(@PathVariable UUID projectId, @PathVariable UUID changeRequestId) {
        return ApiResponse.success(cancelAction.execute(projectId, changeRequestId));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}/apply")
    public ApiResponse<ChangeRequestResponse> apply(@PathVariable UUID projectId, @PathVariable UUID changeRequestId) {
        return ApiResponse.success(applyAction.execute(projectId, changeRequestId));
    }

    @PatchMapping(ProjectBaselineApiPaths.CHANGE_REQUESTS + "/{changeRequestId}/archive")
    public ApiResponse<ChangeRequestResponse> archive(@PathVariable UUID projectId, @PathVariable UUID changeRequestId) {
        return ApiResponse.success(archiveAction.execute(projectId, changeRequestId));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_ITEMS)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChangeRequestItemResponse> createItem(@PathVariable UUID projectId,
                                                             @PathVariable UUID changeRequestId,
                                                             @Valid @RequestBody CreateChangeRequestItemRequest request) {
        return ApiResponse.success(createItemAction.execute(new CreateChangeRequestItemCommand(
                projectId, changeRequestId, request.targetType(), request.targetId(), request.operation(),
                request.summary(), request.beforeSnapshotJson(), request.afterSnapshotJson(), request.applyPayloadJson())));
    }

    @GetMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_ITEMS)
    public ApiResponse<List<ChangeRequestItemResponse>> listItems(@PathVariable UUID projectId,
                                                                  @PathVariable UUID changeRequestId) {
        return ApiResponse.success(itemQueryService.list(projectId, changeRequestId));
    }

    @GetMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_ITEMS + "/{itemId}")
    public ApiResponse<ChangeRequestItemResponse> getItem(@PathVariable UUID projectId,
                                                          @PathVariable UUID changeRequestId,
                                                          @PathVariable UUID itemId) {
        return ApiResponse.success(itemQueryService.get(projectId, changeRequestId, itemId));
    }

    @PutMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_ITEMS + "/{itemId}")
    public ApiResponse<ChangeRequestItemResponse> updateItem(@PathVariable UUID projectId,
                                                             @PathVariable UUID changeRequestId,
                                                             @PathVariable UUID itemId,
                                                             @Valid @RequestBody UpdateChangeRequestItemRequest request) {
        return ApiResponse.success(updateItemAction.execute(new UpdateChangeRequestItemCommand(
                projectId, changeRequestId, itemId, request.targetType(), request.targetId(), request.operation(),
                request.summary(), request.beforeSnapshotJson(), request.afterSnapshotJson(), request.applyPayloadJson())));
    }

    @DeleteMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_ITEMS + "/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable UUID projectId, @PathVariable UUID changeRequestId, @PathVariable UUID itemId) {
        deleteItemAction.execute(projectId, changeRequestId, itemId);
    }

    @GetMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_IMPACT)
    public ApiResponse<ChangeImpactResponse> getImpact(@PathVariable UUID projectId, @PathVariable UUID changeRequestId) {
        return ApiResponse.success(impactQueryService.get(projectId, changeRequestId));
    }

    @PutMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_IMPACT)
    public ApiResponse<ChangeImpactResponse> upsertImpact(@PathVariable UUID projectId,
                                                          @PathVariable UUID changeRequestId,
                                                          @RequestBody UpdateChangeImpactRequest request) {
        return ApiResponse.success(upsertImpactAction.execute(projectId, changeRequestId, request));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_REQUEST_IMPACT + "/calculate")
    public ApiResponse<ChangeImpactResponse> calculateImpact(@PathVariable UUID projectId,
                                                             @PathVariable UUID changeRequestId) {
        return ApiResponse.success(calculateImpactAction.execute(projectId, changeRequestId));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_ORDERS)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChangeOrderResponse> createOrder(@PathVariable UUID projectId,
                                                        @PathVariable UUID changeRequestId,
                                                        @Valid @RequestBody CreateChangeOrderRequest request) {
        return ApiResponse.success(createChangeOrderAction.execute(projectId, changeRequestId, request));
    }

    @GetMapping(ProjectBaselineApiPaths.CHANGE_ORDERS)
    public ApiResponse<List<ChangeOrderResponse>> listOrders(@PathVariable UUID projectId,
                                                             @PathVariable UUID changeRequestId) {
        return ApiResponse.success(changeOrderQueryService.listByChangeRequest(projectId, changeRequestId));
    }

    @GetMapping(ProjectBaselineApiPaths.CHANGE_ORDER_BY_ID)
    public ApiResponse<ChangeOrderResponse> getOrder(@PathVariable UUID projectId, @PathVariable UUID changeOrderId) {
        return ApiResponse.success(changeOrderQueryService.get(projectId, changeOrderId));
    }

    @PutMapping(ProjectBaselineApiPaths.CHANGE_ORDER_BY_ID)
    public ApiResponse<ChangeOrderResponse> updateOrder(@PathVariable UUID projectId, @PathVariable UUID changeOrderId,
                                                        @Valid @RequestBody UpdateChangeOrderRequest request) {
        return ApiResponse.success(updateChangeOrderAction.execute(projectId, changeOrderId, request));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_ORDER_BY_ID + "/approve")
    public ApiResponse<ChangeOrderResponse> approveOrder(@PathVariable UUID projectId, @PathVariable UUID changeOrderId) {
        return ApiResponse.success(approveChangeOrderAction.execute(projectId, changeOrderId));
    }

    @PostMapping(ProjectBaselineApiPaths.CHANGE_ORDER_BY_ID + "/reject")
    public ApiResponse<ChangeOrderResponse> rejectOrder(@PathVariable UUID projectId, @PathVariable UUID changeOrderId,
                                                        @Valid @RequestBody RejectChangeOrderRequest request) {
        return ApiResponse.success(rejectChangeOrderAction.execute(projectId, changeOrderId, request.reason()));
    }

    @PatchMapping(ProjectBaselineApiPaths.CHANGE_ORDER_BY_ID + "/archive")
    public ApiResponse<ChangeOrderResponse> archiveOrder(@PathVariable UUID projectId, @PathVariable UUID changeOrderId) {
        return ApiResponse.success(archiveChangeOrderAction.execute(projectId, changeOrderId));
    }
}
