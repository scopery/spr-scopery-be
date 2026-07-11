package com.company.scopery.modules.aiagent.eventconfig.application.response;

import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;

import java.time.Instant;
import java.util.UUID;

public record EventConfigDetailResponse(
        UUID id,
        String code,
        String name,
        UUID eventDefinitionId,
        String eventDefinitionCode,
        String sourceSystem,
        String eventKey,
        String environment,
        String triggerType,
        UUID agentId,
        String agentName,
        UUID promptVersionId,
        String promptTemplateCode,
        Integer promptVersionNumber,
        UUID modelDeploymentId,
        String modelDeploymentCode,
        String modelDeploymentName,
        String conditionExpression,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static EventConfigDetailResponse from(EventConfig c,
                                                  String eventDefinitionCode,
                                                  String sourceSystem,
                                                  String eventKey,
                                                  String agentName,
                                                  String promptTemplateCode,
                                                  Integer promptVersionNumber,
                                                  String modelDeploymentCode,
                                                  String modelDeploymentName) {
        return new EventConfigDetailResponse(
                c.id(),
                c.code().value(),
                c.name(),
                c.eventDefinitionId(),
                eventDefinitionCode,
                sourceSystem,
                eventKey,
                c.environment().name(),
                c.triggerType().name(),
                c.agentId(),
                agentName,
                c.promptVersionId(),
                promptTemplateCode,
                promptVersionNumber,
                c.modelDeploymentId(),
                modelDeploymentCode,
                modelDeploymentName,
                c.conditionExpression(),
                c.description(),
                c.status().name(),
                c.createdAt(),
                c.updatedAt()
        );
    }
}