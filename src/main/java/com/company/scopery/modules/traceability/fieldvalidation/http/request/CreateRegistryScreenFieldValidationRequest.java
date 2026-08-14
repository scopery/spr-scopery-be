package com.company.scopery.modules.traceability.fieldvalidation.http.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRegistryScreenFieldValidationRequest(
        @NotNull UUID ruleTypeId,
        UUID modeId,
        JsonNode ruleParamJson,
        JsonNode conditionJson,
        String errorMessage,
        String remark,
        int displayOrder) {}
