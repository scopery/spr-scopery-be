package com.company.scopery.modules.aiassistant.workspaceconfig.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AiAssistantWorkspaceConfig {

    private final UUID id;
    private final UUID workspaceId;
    private UUID modelDeploymentId;
    private String modelProvider;
    private String modelName;
    private String systemPromptOverride;
    private BigDecimal temperatureOverride;
    private Integer maxOutputTokensOverride;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiAssistantWorkspaceConfig(UUID id, UUID workspaceId, UUID modelDeploymentId,
            String modelProvider, String modelName, String systemPromptOverride,
            BigDecimal temperatureOverride, Integer maxOutputTokensOverride,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.modelDeploymentId = modelDeploymentId;
        this.modelProvider = modelProvider;
        this.modelName = modelName;
        this.systemPromptOverride = systemPromptOverride;
        this.temperatureOverride = temperatureOverride;
        this.maxOutputTokensOverride = maxOutputTokensOverride;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiAssistantWorkspaceConfig create(UUID workspaceId, UUID modelDeploymentId,
            String modelProvider, String modelName, String systemPromptOverride,
            BigDecimal temperatureOverride, Integer maxOutputTokensOverride) {
        Instant now = Instant.now();
        return new AiAssistantWorkspaceConfig(
                UUID.randomUUID(), workspaceId, modelDeploymentId,
                modelProvider, modelName, systemPromptOverride,
                temperatureOverride, maxOutputTokensOverride, now, now);
    }

    public static AiAssistantWorkspaceConfig reconstitute(UUID id, UUID workspaceId,
            UUID modelDeploymentId, String modelProvider, String modelName,
            String systemPromptOverride, BigDecimal temperatureOverride,
            Integer maxOutputTokensOverride, Instant createdAt, Instant updatedAt) {
        return new AiAssistantWorkspaceConfig(id, workspaceId, modelDeploymentId,
                modelProvider, modelName, systemPromptOverride,
                temperatureOverride, maxOutputTokensOverride, createdAt, updatedAt);
    }

    public void update(UUID modelDeploymentId, String modelProvider, String modelName,
            String systemPromptOverride, BigDecimal temperatureOverride,
            Integer maxOutputTokensOverride) {
        this.modelDeploymentId = modelDeploymentId;
        this.modelProvider = modelProvider;
        this.modelName = modelName;
        this.systemPromptOverride = systemPromptOverride;
        this.temperatureOverride = temperatureOverride;
        this.maxOutputTokensOverride = maxOutputTokensOverride;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID workspaceId() { return workspaceId; }
    public UUID modelDeploymentId() { return modelDeploymentId; }
    public String modelProvider() { return modelProvider; }
    public String modelName() { return modelName; }
    public String systemPromptOverride() { return systemPromptOverride; }
    public BigDecimal temperatureOverride() { return temperatureOverride; }
    public Integer maxOutputTokensOverride() { return maxOutputTokensOverride; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
