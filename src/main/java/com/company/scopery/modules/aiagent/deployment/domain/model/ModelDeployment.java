package com.company.scopery.modules.aiagent.deployment.domain.model;

import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.valueobject.ModelDeploymentCode;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ModelDeployment {

    private static final BigDecimal TEMPERATURE_MIN = BigDecimal.ZERO;
    private static final BigDecimal TEMPERATURE_MAX = new BigDecimal("2.0");

    private final UUID id;
    private final UUID modelId;
    private String name;
    private final ModelDeploymentCode code;
    private final ModelDeploymentEnvironment environment;
    private String providerDeploymentId;
    private String endpointUrl;
    private BigDecimal defaultTemperature;
    private Integer defaultMaxOutputTokens;
    private boolean isDefault;
    private String description;
    private ModelDeploymentStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private ModelDeployment(UUID id, UUID modelId, String name, ModelDeploymentCode code,
                            ModelDeploymentEnvironment environment, String providerDeploymentId,
                            String endpointUrl, BigDecimal defaultTemperature,
                            Integer defaultMaxOutputTokens, boolean isDefault,
                            String description, ModelDeploymentStatus status,
                            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.modelId = modelId;
        this.name = name;
        this.code = code;
        this.environment = environment;
        this.providerDeploymentId = providerDeploymentId;
        this.endpointUrl = endpointUrl;
        this.defaultTemperature = defaultTemperature;
        this.defaultMaxOutputTokens = defaultMaxOutputTokens;
        this.isDefault = isDefault;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ModelDeployment create(UUID modelId, String name, ModelDeploymentCode code,
                                          ModelDeploymentEnvironment environment,
                                          String providerDeploymentId, String endpointUrl,
                                          BigDecimal defaultTemperature,
                                          Integer defaultMaxOutputTokens,
                                          boolean isDefault, String description) {
        validateModelId(modelId);
        validateName(name);
        validateProviderDeploymentId(providerDeploymentId);
        validateDefaultTemperature(defaultTemperature);
        validateDefaultMaxOutputTokens(defaultMaxOutputTokens);
        Instant now = Instant.now();
        return new ModelDeployment(UUID.randomUUID(), modelId, name, code, environment,
                providerDeploymentId, endpointUrl, defaultTemperature, defaultMaxOutputTokens,
                isDefault, description, ModelDeploymentStatus.ACTIVE, now, now);
    }

    public static ModelDeployment reconstitute(UUID id, UUID modelId, String name,
                                               ModelDeploymentCode code,
                                               ModelDeploymentEnvironment environment,
                                               String providerDeploymentId, String endpointUrl,
                                               BigDecimal defaultTemperature,
                                               Integer defaultMaxOutputTokens, boolean isDefault,
                                               String description, ModelDeploymentStatus status,
                                               Instant createdAt, Instant updatedAt) {
        return new ModelDeployment(id, modelId, name, code, environment, providerDeploymentId,
                endpointUrl, defaultTemperature, defaultMaxOutputTokens, isDefault, description,
                status, createdAt, updatedAt);
    }

    public void update(String name, String providerDeploymentId, String endpointUrl,
                       BigDecimal defaultTemperature, Integer defaultMaxOutputTokens,
                       boolean isDefault, String description) {
        validateName(name);
        validateProviderDeploymentId(providerDeploymentId);
        validateDefaultTemperature(defaultTemperature);
        validateDefaultMaxOutputTokens(defaultMaxOutputTokens);
        this.name = name;
        this.providerDeploymentId = providerDeploymentId;
        this.endpointUrl = endpointUrl;
        this.defaultTemperature = defaultTemperature;
        this.defaultMaxOutputTokens = defaultMaxOutputTokens;
        this.isDefault = isDefault;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == ModelDeploymentStatus.DEPRECATED) {
            throw new IllegalStateException("DEPRECATED_MODEL_DEPLOYMENT_CANNOT_BE_ACTIVATED");
        }
        this.status = ModelDeploymentStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = ModelDeploymentStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void setDefault() {
        this.isDefault = true;
        this.updatedAt = Instant.now();
    }

    public void unsetDefault() {
        this.isDefault = false;
        this.updatedAt = Instant.now();
    }

    private static void validateModelId(UUID modelId) {
        if (modelId == null) {
            throw new IllegalArgumentException("Model ID is required");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Deployment name is required");
        }
    }

    private static void validateProviderDeploymentId(String providerDeploymentId) {
        if (providerDeploymentId == null || providerDeploymentId.isBlank()) {
            throw new IllegalArgumentException("Provider deployment ID is required");
        }
    }

    private static void validateDefaultTemperature(BigDecimal temperature) {
        if (temperature == null) return;
        if (temperature.compareTo(TEMPERATURE_MIN) < 0 || temperature.compareTo(TEMPERATURE_MAX) > 0) {
            throw new IllegalArgumentException(
                    "Default temperature must be between 0.0 and 2.0, got: " + temperature);
        }
    }

    private static void validateDefaultMaxOutputTokens(Integer maxOutputTokens) {
        if (maxOutputTokens == null) return;
        if (maxOutputTokens <= 0) {
            throw new IllegalArgumentException(
                    "Default max output tokens must be greater than 0, got: " + maxOutputTokens);
        }
    }

    public UUID id() { return id; }
    public UUID modelId() { return modelId; }
    public String name() { return name; }
    public ModelDeploymentCode code() { return code; }
    public ModelDeploymentEnvironment environment() { return environment; }
    public String providerDeploymentId() { return providerDeploymentId; }
    public String endpointUrl() { return endpointUrl; }
    public BigDecimal defaultTemperature() { return defaultTemperature; }
    public Integer defaultMaxOutputTokens() { return defaultMaxOutputTokens; }
    public boolean isDefault() { return isDefault; }
    public String description() { return description; }
    public ModelDeploymentStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
