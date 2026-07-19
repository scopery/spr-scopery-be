package com.company.scopery.modules.aiagent.aimodel.domain.model;

import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelType;
import com.company.scopery.modules.aiagent.aimodel.domain.valueobject.AiModelCode;

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
    private boolean supportsChat;
    private boolean supportsEmbedding;
    private boolean supportsToolCalling;
    private boolean supportsJsonMode;
    private Integer contextWindowTokens;
    private Integer maxOutputTokens;
    private String modelFamily;
    private String capabilitiesJson;
    private AiModelStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private AiModel(UUID id, UUID providerId, String name, AiModelCode code,
                    String providerModelId, AiModelType type, String description,
                    boolean supportsChat, boolean supportsEmbedding,
                    boolean supportsToolCalling, boolean supportsJsonMode,
                    Integer contextWindowTokens, Integer maxOutputTokens,
                    String modelFamily, String capabilitiesJson,
                    AiModelStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.code = code;
        this.providerModelId = providerModelId;
        this.type = type;
        this.description = description;
        this.supportsChat = supportsChat;
        this.supportsEmbedding = supportsEmbedding;
        this.supportsToolCalling = supportsToolCalling;
        this.supportsJsonMode = supportsJsonMode;
        this.contextWindowTokens = contextWindowTokens;
        this.maxOutputTokens = maxOutputTokens;
        this.modelFamily = modelFamily;
        this.capabilitiesJson = capabilitiesJson;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AiModel create(UUID providerId, String name, AiModelCode code,
                                  String providerModelId, AiModelType type, String description) {
        return create(providerId, name, code, providerModelId, type, description,
                false, false, false, false, null, null, null, null);
    }

    public static AiModel create(UUID providerId, String name, AiModelCode code,
                                  String providerModelId, AiModelType type, String description,
                                  Boolean supportsChat, Boolean supportsEmbedding,
                                  Boolean supportsToolCalling, Boolean supportsJsonMode,
                                  Integer contextWindowTokens, Integer maxOutputTokens,
                                  String modelFamily, String capabilitiesJson) {
        validateProviderId(providerId);
        validateName(name);
        validateProviderModelId(providerModelId);
        validateType(type);
        Instant now = Instant.now();
        return new AiModel(UUID.randomUUID(), providerId, name, code, providerModelId,
                           type, description,
                           Boolean.TRUE.equals(supportsChat),
                           Boolean.TRUE.equals(supportsEmbedding),
                           Boolean.TRUE.equals(supportsToolCalling),
                           Boolean.TRUE.equals(supportsJsonMode),
                           contextWindowTokens, maxOutputTokens, modelFamily, capabilitiesJson,
                           AiModelStatus.ACTIVE, now, now);
    }

    public static AiModel reconstitute(UUID id, UUID providerId, String name, AiModelCode code,
                                        String providerModelId, AiModelType type, String description,
                                        AiModelStatus status, Instant createdAt, Instant updatedAt) {
        return reconstitute(id, providerId, name, code, providerModelId, type, description,
                false, false, false, false, null, null, null, null, status, createdAt, updatedAt);
    }

    public static AiModel reconstitute(UUID id, UUID providerId, String name, AiModelCode code,
                                        String providerModelId, AiModelType type, String description,
                                        boolean supportsChat, boolean supportsEmbedding,
                                        boolean supportsToolCalling, boolean supportsJsonMode,
                                        Integer contextWindowTokens, Integer maxOutputTokens,
                                        String modelFamily, String capabilitiesJson,
                                        AiModelStatus status, Instant createdAt, Instant updatedAt) {
        return new AiModel(id, providerId, name, code, providerModelId, type, description,
                           supportsChat, supportsEmbedding, supportsToolCalling, supportsJsonMode,
                           contextWindowTokens, maxOutputTokens, modelFamily, capabilitiesJson,
                           status, createdAt, updatedAt);
    }

    public void update(String name, String providerModelId, AiModelType type, String description) {
        update(name, providerModelId, type, description,
                this.supportsChat, this.supportsEmbedding, this.supportsToolCalling, this.supportsJsonMode,
                this.contextWindowTokens, this.maxOutputTokens, this.modelFamily, this.capabilitiesJson);
    }

    public void update(String name, String providerModelId, AiModelType type, String description,
                       Boolean supportsChat, Boolean supportsEmbedding,
                       Boolean supportsToolCalling, Boolean supportsJsonMode,
                       Integer contextWindowTokens, Integer maxOutputTokens,
                       String modelFamily, String capabilitiesJson) {
        validateName(name);
        validateProviderModelId(providerModelId);
        validateType(type);
        this.name = name;
        this.providerModelId = providerModelId;
        this.type = type;
        this.description = description;
        if (supportsChat != null) this.supportsChat = supportsChat;
        if (supportsEmbedding != null) this.supportsEmbedding = supportsEmbedding;
        if (supportsToolCalling != null) this.supportsToolCalling = supportsToolCalling;
        if (supportsJsonMode != null) this.supportsJsonMode = supportsJsonMode;
        this.contextWindowTokens = contextWindowTokens;
        this.maxOutputTokens = maxOutputTokens;
        this.modelFamily = modelFamily;
        this.capabilitiesJson = capabilitiesJson;
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
    public boolean supportsChat() { return supportsChat; }
    public boolean supportsEmbedding() { return supportsEmbedding; }
    public boolean supportsToolCalling() { return supportsToolCalling; }
    public boolean supportsJsonMode() { return supportsJsonMode; }
    public Integer contextWindowTokens() { return contextWindowTokens; }
    public Integer maxOutputTokens() { return maxOutputTokens; }
    public String modelFamily() { return modelFamily; }
    public String capabilitiesJson() { return capabilitiesJson; }
    public AiModelStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
