package com.company.scopery.modules.traceability.usecase.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Nested DTO shapes for create / bulk create.
 * Processed server-side after the use-case shell is created (no FE create loop).
 */
public final class NestedUseCasePartsRequest {
    private NestedUseCasePartsRequest() {}

    public record NestedFlowRequest(
            @NotBlank @Schema(allowableValues = {"MAIN", "ALTERNATIVE", "EXCEPTION"}, example = "MAIN") String flowType,
            String name,
            String conditionText,
            List<NestedFlowStepRequest> steps
    ) {}

    public record NestedFlowStepRequest(
            @NotBlank @Schema(allowableValues = {"USER_ACTION", "SYSTEM_ACTION", "CONDITION", "NAVIGATION", "RESULT", "ERROR"}, example = "USER_ACTION") String stepType,
            @Schema(description = "Step content (preferred field)") String contentJson,
            @Schema(description = "Alias for contentJson — used when contentJson is omitted (JSON import)") String content,
            Integer displayOrder
    ) {
        public String resolvedContentJson() {
            if (contentJson != null && !contentJson.isBlank()) return contentJson;
            return content;
        }

        public int resolvedDisplayOrder(int fallback) {
            return displayOrder != null ? displayOrder : fallback;
        }
    }

    public record NestedConditionRequest(
            @NotBlank @Schema(allowableValues = {"PRECONDITION", "ASSUMPTION", "SUCCESS_POSTCONDITION", "FAILURE_POSTCONDITION"}, example = "PRECONDITION") String conditionType,
            @NotBlank String content,
            Integer displayOrder
    ) {
        public int resolvedDisplayOrder(int fallback) {
            return displayOrder != null ? displayOrder : fallback;
        }
    }

    public record NestedBusinessRuleRequest(
            @NotBlank String ruleCode,
            @NotBlank String description,
            Integer displayOrder
    ) {
        public int resolvedDisplayOrder(int fallback) {
            return displayOrder != null ? displayOrder : fallback;
        }
    }

    public record NestedAcceptanceCriterionRequest(
            @NotBlank String title,
            String givenText,
            String whenText,
            String thenText,
            Integer displayOrder
    ) {
        public int resolvedDisplayOrder(int fallback) {
            return displayOrder != null ? displayOrder : fallback;
        }
    }
}
