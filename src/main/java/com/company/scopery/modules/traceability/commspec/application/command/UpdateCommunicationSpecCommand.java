package com.company.scopery.modules.traceability.commspec.application.command;

import java.util.UUID;

public record UpdateCommunicationSpecCommand(
        UUID workspaceId,
        UUID applicationId,
        UUID communicationSpecId,
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
