package com.company.scopery.modules.traceability.commspec.application.command;

import java.util.UUID;

public record CreateCommunicationSpecCommand(
        UUID workspaceId,
        UUID applicationId,
        String code,
        String name,
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
