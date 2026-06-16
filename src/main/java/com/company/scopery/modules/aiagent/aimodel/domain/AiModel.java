package com.company.scopery.modules.aiagent.aimodel.domain;

import java.time.Instant;
import java.util.UUID;

public class AiModel {

    private final UUID id;
    private final UUID providerId;
    private String name;
    private final AiModelCode code;
    private String providerModelId;
    private AiModelType type;
    private String description;
    private AiModelStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiModel(UUID id, UUID providerId, String name, AiModelCode code,
                    String providerModelId, AiModelType type, String description,
                    AiModelStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.code = code;
        this.providerModelId = providerModelId;
        this.type = type;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiModel create(UUID providerId, String name, AiModelCode code,
                                  String providerModelId, AiModelType type, String description) {
        validateProviderId(providerId);
        validateName(name);
        validateProviderModelId(providerModelId);
        validateType(type);
        Instant now = Instant.now();
        return new AiModel(UUID.randomUUID(), providerId, name, code, providerModelId,
                           type, description, AiModelStatus.ACTIVE, now, now);
    }

    public static AiModel reconstitute(UUID id, UUID providerId, String name, AiModelCode code,
                                        String providerModelId, AiModelType type, String description,
                                        AiModelStatus status, Instant createdAt, Instant updatedAt) {
        return new AiModel(id, providerId, name, code, providerModelId, type, description,
                           status, createdAt, updatedAt);
    }

    public void update(String name, String providerModelId, AiModelType type, String description) {
        validateName(name);
        validateProviderModelId(providerModelId);
        validateType(type);
        this.name = name;
        this.providerModelId = providerModelId;
        this.type = type;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == AiModelStatus.DEPRECATED) {
            throw new IllegalStateException("DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED");
        }
        this.status = AiModelStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = AiModelStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    private static void validateProviderId(UUID providerId) {
        if (providerId == null) {
            throw new IllegalArgumentException("Provider ID is required");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("AI model name is required");
        }
    }

    private static void validateProviderModelId(String providerModelId) {
        if (providerModelId == null || providerModelId.isBlank()) {
            throw new IllegalArgumentException("Provider model ID is required");
        }
    }

    private static void validateType(AiModelType type) {
        if (type == null) {
            throw new IllegalArgumentException("AI model type is required");
        }
    }

    public UUID id() { return id; }
    public UUID providerId() { return providerId; }
    public String name() { return name; }
    public AiModelCode code() { return code; }
    public String providerModelId() { return providerModelId; }
    public AiModelType type() { return type; }
    public String description() { return description; }
    public AiModelStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}