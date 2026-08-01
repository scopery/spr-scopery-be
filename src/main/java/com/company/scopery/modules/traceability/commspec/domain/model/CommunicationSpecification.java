package com.company.scopery.modules.traceability.commspec.domain.model;

import com.company.scopery.modules.traceability.commspec.domain.enums.CommunicationSpecStatus;

import java.time.Instant;
import java.util.UUID;

public record CommunicationSpecification(
        UUID id,
        UUID applicationId,
        UUID workspaceId,
        String code,
        String name,
        String description,
        CommunicationSpecStatus status,
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
    public static CommunicationSpecification create(
            UUID applicationId,
            UUID workspaceId,
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
    ) {
        return new CommunicationSpecification(
                UUID.randomUUID(), applicationId, workspaceId, code, name, description,
                CommunicationSpecStatus.DRAFT, triggerName, triggerKey, triggerTiming,
                conditionJson, suppressionConditionJson, deliveryPolicyJson,
                inAppContractJson, emailContractJson, recipientsJson, ownerId,
                0, null, null, null);
    }

    public CommunicationSpecification withUpdated(
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
    ) {
        return new CommunicationSpecification(
                id, applicationId, workspaceId, code, name, description, status,
                triggerName, triggerKey, triggerTiming, conditionJson, suppressionConditionJson,
                deliveryPolicyJson, inAppContractJson, emailContractJson, recipientsJson, ownerId,
                version, createdAt, updatedAt, archivedAt);
    }

    public CommunicationSpecification withStatus(CommunicationSpecStatus next) {
        return new CommunicationSpecification(
                id, applicationId, workspaceId, code, name, description, next,
                triggerName, triggerKey, triggerTiming, conditionJson, suppressionConditionJson,
                deliveryPolicyJson, inAppContractJson, emailContractJson, recipientsJson, ownerId,
                version, createdAt, updatedAt, archivedAt);
    }

    public CommunicationSpecification archive() {
        return new CommunicationSpecification(
                id, applicationId, workspaceId, code, name, description, CommunicationSpecStatus.ARCHIVED,
                triggerName, triggerKey, triggerTiming, conditionJson, suppressionConditionJson,
                deliveryPolicyJson, inAppContractJson, emailContractJson, recipientsJson, ownerId,
                version, createdAt, updatedAt, Instant.now());
    }
}
