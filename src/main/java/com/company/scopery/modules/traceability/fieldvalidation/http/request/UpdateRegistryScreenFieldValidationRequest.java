package com.company.scopery.modules.traceability.fieldvalidation.http.request;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record UpdateRegistryScreenFieldValidationRequest(
        UUID modeId,
        JsonNode ruleParamJson,
        JsonNode conditionJson,
        String errorMessage,
        String remark,
        int displayOrder) {}
