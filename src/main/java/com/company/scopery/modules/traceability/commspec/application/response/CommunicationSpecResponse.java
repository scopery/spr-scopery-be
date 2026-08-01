package com.company.scopery.modules.traceability.commspec.application.response;

import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecification;

import java.time.Instant;
import java.util.UUID;

public record CommunicationSpecResponse(
        UUID id,
        UUID applicationId,
        UUID workspaceId,
        String code,
        String name,
        String description,
        String status,
        String triggerName,
        String triggerKey,
        String triggerTiming,
        String conditionJson,
        String suppressionConditionJson,
        String deliveryPolicyJson,
        String inAppContractJson,
        String emailContractJson,
        String recipientsJson,
        UUID ownerId,
        int version,
        Instant createdAt,
        Instant updatedAt,
        Instant archivedAt
) {
    public static CommunicationSpecResponse from(CommunicationSpecification s) {
        return new CommunicationSpecResponse(
                s.id(), s.applicationId(), s.workspaceId(), s.code(), s.name(), s.description(),
                s.status().name(), s.triggerName(), s.triggerKey(), s.triggerTiming(),
                s.conditionJson(), s.suppressionConditionJson(), s.deliveryPolicyJson(),
                s.inAppContractJson(), s.emailContractJson(), s.recipientsJson(), s.ownerId(),
                s.version(), s.createdAt(), s.updatedAt(), s.archivedAt());
    }
}
