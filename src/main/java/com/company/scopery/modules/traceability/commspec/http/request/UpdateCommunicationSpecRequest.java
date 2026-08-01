package com.company.scopery.modules.traceability.commspec.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateCommunicationSpecRequest(
        @NotBlank String name,
        String description,
        String triggerName,
        String triggerKey,
        String triggerTiming,
        String conditionJson,
        String suppressionConditionJson,
        String deliveryPolicyJson,
        String inAppContractJson,
        String emailContractJson,
        String recipientsJson,
        UUID ownerId
) {}
