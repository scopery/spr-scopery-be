package com.company.scopery.modules.aiagent.eventconfig.domain.model;

import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventTriggerType;
import com.company.scopery.modules.aiagent.eventconfig.domain.valueobject.EventConfigCode;

import java.time.Instant;
import java.util.UUID;

public class EventConfig {

    private final UUID id;
    private final EventConfigCode code;
    private String name;
    private final UUID eventDefinitionId;
    private final EventConfigEnvironment environment;
    private EventTriggerType triggerType;
    private UUID agentId;
    private UUID promptVersionId;
    private UUID modelDeploymentId;
    private String conditionExpression;
    private String description;
    private String inputMappingJson;
    private String outputMappingJson;
    private Instant activatedAt;
    private String activatedBy;
    private Instant deactivatedAt;
    private String deactivatedBy;
    private EventConfigStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private EventConfig(UUID id, EventConfigCode code, String name,
                        UUID eventDefinitionId, EventConfigEnvironment environment,
                        EventTriggerType triggerType, UUID agentId, UUID promptVersionId,
                        UUID modelDeploymentId, String conditionExpression, String description,
                        String inputMappingJson, String outputMappingJson,
                        Instant activatedAt, String activatedBy,
                        Instant deactivatedAt, String deactivatedBy,
                        EventConfigStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.eventDefinitionId = eventDefinitionId;
        this.environment = environment;
        this.triggerType = triggerType;
        this.agentId = agentId;
        this.promptVersionId = promptVersionId;
        this.modelDeploymentId = modelDeploymentId;
        this.conditionExpression = conditionExpression;
        this.description = description;
        this.inputMappingJson = inputMappingJson;
        this.outputMappingJson = outputMappingJson;
        this.activatedAt = activatedAt;
        this.activatedBy = activatedBy;
        this.deactivatedAt = deactivatedAt;
        this.deactivatedBy = deactivatedBy;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EventConfig create(EventConfigCode code, String name,
                                     UUID eventDefinitionId, EventConfigEnvironment environment,
                                     EventTriggerType triggerType, UUID agentId,
                                     UUID promptVersionId, UUID modelDeploymentId,
                                     String conditionExpression, String description,
                                     String inputMappingJson, String outputMappingJson) {
        validateName(name);
        validateRequiredIds(eventDefinitionId, agentId, promptVersionId, modelDeploymentId);
        Instant now = Instant.now();
        return new EventConfig(UUID.randomUUID(), code, name, eventDefinitionId, environment,
                triggerType, agentId, promptVersionId, modelDeploymentId, conditionExpression,
                description, inputMappingJson, outputMappingJson,
                now, "SYSTEM", null, null,
                EventConfigStatus.ACTIVE, now, now);
    }

    public static EventConfig create(EventConfigCode code, String name,
                                     UUID eventDefinitionId, EventConfigEnvironment environment,
                                     EventTriggerType triggerType, UUID agentId,
                                     UUID promptVersionId, UUID modelDeploymentId,
                                     String conditionExpression, String description) {
        return create(code, name, eventDefinitionId, environment, triggerType, agentId,
                promptVersionId, modelDeploymentId, conditionExpression, description, null, null);
    }

    public static EventConfig reconstitute(UUID id, EventConfigCode code, String name,
                                           UUID eventDefinitionId, EventConfigEnvironment environment,
                                           EventTriggerType triggerType, UUID agentId,
                                           UUID promptVersionId, UUID modelDeploymentId,
                                           String conditionExpression, String description,
                                           EventConfigStatus status, Instant createdAt, Instant updatedAt) {
        return reconstitute(id, code, name, eventDefinitionId, environment, triggerType,
                agentId, promptVersionId, modelDeploymentId, conditionExpression, description,
                null, null, null, null, null, null, status, createdAt, updatedAt);
    }

    public static EventConfig reconstitute(UUID id, EventConfigCode code, String name,
                                           UUID eventDefinitionId, EventConfigEnvironment environment,
                                           EventTriggerType triggerType, UUID agentId,
                                           UUID promptVersionId, UUID modelDeploymentId,
                                           String conditionExpression, String description,
                                           String inputMappingJson, String outputMappingJson,
                                           Instant activatedAt, String activatedBy,
                                           Instant deactivatedAt, String deactivatedBy,
                                           EventConfigStatus status, Instant createdAt, Instant updatedAt) {
        return new EventConfig(id, code, name, eventDefinitionId, environment, triggerType,
                agentId, promptVersionId, modelDeploymentId, conditionExpression, description,
                inputMappingJson, outputMappingJson, activatedAt, activatedBy,
                deactivatedAt, deactivatedBy, status, createdAt, updatedAt);
    }

    public void update(String name, EventTriggerType triggerType, UUID agentId,
                       UUID promptVersionId, UUID modelDeploymentId,
                       String conditionExpression, String description,
                       String inputMappingJson, String outputMappingJson) {
        validateName(name);
        validateRequiredIds(this.eventDefinitionId, agentId, promptVersionId, modelDeploymentId);
        this.name = name;
        this.triggerType = triggerType;
        this.agentId = agentId;
        this.promptVersionId = promptVersionId;
        this.modelDeploymentId = modelDeploymentId;
        this.conditionExpression = conditionExpression;
        this.description = description;
        this.inputMappingJson = inputMappingJson;
        this.outputMappingJson = outputMappingJson;
        this.updatedAt = Instant.now();
    }

    public void update(String name, EventTriggerType triggerType, UUID agentId,
                       UUID promptVersionId, UUID modelDeploymentId,
                       String conditionExpression, String description) {
        update(name, triggerType, agentId, promptVersionId, modelDeploymentId,
                conditionExpression, description, this.inputMappingJson, this.outputMappingJson);
    }

    public void activate() {
        activate("SYSTEM");
    }

    public void activate(String activatedBy) {
        if (this.status == EventConfigStatus.DEPRECATED) {
            throw new IllegalStateException("Deprecated event configuration cannot be activated again");
        }
        this.status = EventConfigStatus.ACTIVE;
        this.activatedAt = Instant.now();
        this.activatedBy = activatedBy != null && !activatedBy.isBlank() ? activatedBy : "SYSTEM";
        this.deactivatedAt = null;
        this.deactivatedBy = null;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        deactivate("SYSTEM");
    }

    public void deactivate(String deactivatedBy) {
        this.status = EventConfigStatus.INACTIVE;
        this.deactivatedAt = Instant.now();
        this.deactivatedBy = deactivatedBy != null && !deactivatedBy.isBlank() ? deactivatedBy : "SYSTEM";
        this.updatedAt = Instant.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event configuration name is required");
        }
    }

    private static void validateRequiredIds(UUID eventDefinitionId, UUID agentId,
                                            UUID promptVersionId, UUID modelDeploymentId) {
        if (eventDefinitionId == null) throw new IllegalArgumentException("eventDefinitionId is required");
        if (agentId == null) throw new IllegalArgumentException("agentId is required");
        if (promptVersionId == null) throw new IllegalArgumentException("promptVersionId is required");
        if (modelDeploymentId == null) throw new IllegalArgumentException("modelDeploymentId is required");
    }

    public UUID id() { return id; }
    public EventConfigCode code() { return code; }
    public String name() { return name; }
    public UUID eventDefinitionId() { return eventDefinitionId; }
    public EventConfigEnvironment environment() { return environment; }
    public EventTriggerType triggerType() { return triggerType; }
    public UUID agentId() { return agentId; }
    public UUID promptVersionId() { return promptVersionId; }
    public UUID modelDeploymentId() { return modelDeploymentId; }
    public String conditionExpression() { return conditionExpression; }
    public String description() { return description; }
    public String inputMappingJson() { return inputMappingJson; }
    public String outputMappingJson() { return outputMappingJson; }
    public Instant activatedAt() { return activatedAt; }
    public String activatedBy() { return activatedBy; }
    public Instant deactivatedAt() { return deactivatedAt; }
    public String deactivatedBy() { return deactivatedBy; }
    public EventConfigStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
