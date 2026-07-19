package com.company.scopery.modules.ratecard.resolution.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Preview-only estimated labor cost = estimateHours × adjustedCostRate.
 * Not persisted as official project finance.
 */
@Schema(description = "Preview of estimated labor cost for a task based on resolved rates. Not persisted as official project finance.")
public record TaskRatePreviewResponse(
        @Schema(description = "Task for which the rate preview was computed", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID taskId,

        @Schema(description = "Estimated number of hours for the task", example = "8.00")
        BigDecimal estimateHours,

        @Schema(description = "Resolved rate snapshot used for the cost calculation")
        RateSnapshotResponse rateSnapshot,

        @Schema(description = "Estimated labor cost preview computed as estimateHours x adjustedCostRate", example = "662.40")
        BigDecimal estimatedLaborCostPreview,

        @Schema(description = "Human-readable label describing this preview result", example = "estimated labor cost preview")
        String label
) {
    public static final String LABEL = "estimated labor cost preview";
}
