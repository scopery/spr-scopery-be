package com.company.scopery.modules.traceability.functionalitem.http.request;

import com.company.scopery.modules.traceability.businessrule.http.request.CreateBusinessRuleRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateFunctionalItemRequest(
        @NotBlank String code,
        @NotBlank String title,
        String description,
        @NotNull @Schema(allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "MEDIUM") String priority,
        @NotNull @Schema(allowableValues = {"FUNCTIONAL", "USER_STORY", "USE_CASE"}, example = "FUNCTIONAL") String type,
        List<String> acceptanceCriteria,
        UUID workspaceId,
        UUID moduleId,
        @Size(max = 50) @Schema(description = "Optional nested business rules created with the FR in one transaction (max 50)")
        List<@Valid CreateBusinessRuleRequest> businessRules
) {}
