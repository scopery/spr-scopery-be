package com.company.scopery.modules.estimation.preview.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.estimation.shared.constant.EstimationApiPaths;
import com.company.scopery.modules.ratecard.resolution.application.action.PreviewTaskRateAction;
import com.company.scopery.modules.ratecard.resolution.application.query.PreviewTaskRateQuery;
import com.company.scopery.modules.ratecard.resolution.application.response.TaskRatePreviewResponse;
import com.company.scopery.modules.ratecard.resolution.http.request.PreviewTaskRateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Thin estimation-module facade over rate-card preview engine.
 */
@RestController
@RequestMapping(EstimationApiPaths.PREVIEW_RATE_IMPACT)
@Tag(name = "Estimation - Rate Impact Preview")
public class EstimationRateImpactPreviewController {

    private final PreviewTaskRateAction previewTaskRateAction;

    public EstimationRateImpactPreviewController(PreviewTaskRateAction previewTaskRateAction) {
        this.previewTaskRateAction = previewTaskRateAction;
    }

    @PostMapping
    @Operation(summary = "Preview rate impact on task labor cost (delegates to rate resolution engine)")
    public ApiResponse<TaskRatePreviewResponse> preview(
            @PathVariable UUID projectId,
            @Valid @RequestBody PreviewTaskRateRequest request) {
        return ApiResponse.success(previewTaskRateAction.execute(new PreviewTaskRateQuery(
                request.taskId(),
                request.workspaceId(),
                request.costRoleId(),
                request.costRoleCode(),
                request.targetDate(),
                request.currencyCode())));
    }
}
