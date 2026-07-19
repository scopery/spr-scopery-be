package com.company.scopery.modules.ratecard.resolution.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.ratecard.resolution.application.action.PreviewTaskRateAction;
import com.company.scopery.modules.ratecard.resolution.application.action.ResolveRateAction;
import com.company.scopery.modules.ratecard.resolution.application.query.PreviewTaskRateQuery;
import com.company.scopery.modules.ratecard.resolution.application.query.ResolveRateQuery;
import com.company.scopery.modules.ratecard.resolution.application.response.RateSnapshotResponse;
import com.company.scopery.modules.ratecard.resolution.application.response.TaskRatePreviewResponse;
import com.company.scopery.modules.ratecard.resolution.http.request.PreviewTaskRateRequest;
import com.company.scopery.modules.ratecard.resolution.http.request.ResolveRateRequest;
import com.company.scopery.modules.ratecard.shared.constant.RateCardApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Rate Card - Resolution")
public class RateResolutionController {

    private final ResolveRateAction resolveRateAction;
    private final PreviewTaskRateAction previewTaskRateAction;

    public RateResolutionController(ResolveRateAction resolveRateAction,
                                    PreviewTaskRateAction previewTaskRateAction) {
        this.resolveRateAction = resolveRateAction;
        this.previewTaskRateAction = previewTaskRateAction;
    }

    @PostMapping(RateCardApiPaths.RESOLVE)
    @Operation(summary = "Resolve applicable rate snapshot")
    public ApiResponse<RateSnapshotResponse> resolve(@Valid @RequestBody ResolveRateRequest request) {
        return ApiResponse.success(resolveRateAction.execute(new ResolveRateQuery(
                request.workspaceId(), request.organizationId(), request.projectId(),
                request.costRoleId(), request.costRoleCode(), request.targetDate(),
                request.currencyCode(), request.rateType())));
    }

    @PostMapping(RateCardApiPaths.PREVIEW_TASK_RATE)
    @Operation(summary = "Preview estimated labor cost for a task (preview only)")
    public ApiResponse<TaskRatePreviewResponse> previewTaskRate(@Valid @RequestBody PreviewTaskRateRequest request) {
        return ApiResponse.success(previewTaskRateAction.execute(new PreviewTaskRateQuery(
                request.taskId(), request.workspaceId(), request.costRoleId(),
                request.costRoleCode(), request.targetDate(), request.currencyCode())));
    }
}
