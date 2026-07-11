package com.company.scopery.modules.aiagent.capability.domain.model;

import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterCapabilityStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterIfNullBehavior;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import com.company.scopery.modules.aiagent.capability.domain.valueobject.ModelParameterName;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ModelParameterCapability {

    private final UUID id;
    private final UUID modelId;
    private final ModelParameterName parameterName;
    private String apiParameterKey;
    private ModelParameterSupportStatus supportStatus;
    private ModelParameterValueType valueType;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String defaultValue;
    private boolean nullable;
    private ModelParameterIfNullBehavior ifNullBehavior;
    private String description;
    private ModelParameterCapabilityStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private ModelParameterCapability(UUID id, UUID modelId, ModelParameterName parameterName,
                                     String apiParameterKey, ModelParameterSupportStatus supportStatus,
                                     ModelParameterValueType valueType, BigDecimal minValue,
                                     BigDecimal maxValue, String defaultValue, boolean nullable,
                                     ModelParameterIfNullBehavior ifNullBehavior, String description,
                                     ModelParameterCapabilityStatus status,
                                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.modelId = modelId;
        this.parameterName = parameterName;
        this.apiParameterKey = apiParameterKey;
        this.supportStatus = supportStatus;
        this.valueType = valueType;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.nullable = nullable;
        this.ifNullBehavior = ifNullBehavior;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ModelParameterCapability create(UUID modelId, ModelParameterName parameterName,
                                                  String apiParameterKey,
                                                  ModelParameterSupportStatus supportStatus,
                                                  ModelParameterValueType valueType,
                                                  BigDecimal minValue, BigDecimal maxValue,
                                                  String defaultValue, boolean nullable,
                                                  ModelParameterIfNullBehavior ifNullBehavior,
                                                  String description) {
        validateModelId(modelId);
        validateApiParameterKey(apiParameterKey, supportStatus);
        validateMinMax(valueType, minValue, maxValue);
        validateNullableBehavior(nullable, ifNullBehavior);
        Instant now = Instant.now();
        return new ModelParameterCapability(
                UUID.randomUUID(), modelId, parameterName,
                apiParameterKey, supportStatus, valueType,
                minValue, maxValue, defaultValue, nullable, ifNullBehavior, description,
                ModelParameterCapabilityStatus.ACTIVE, now, now);
    }

    public static ModelParameterCapability reconstitute(UUID id, UUID modelId,
                                                        ModelParameterName parameterName,
                                                        String apiParameterKey,
                                                        ModelParameterSupportStatus supportStatus,
                                                        ModelParameterValueType valueType,
                                                        BigDecimal minValue, BigDecimal maxValue,
                                                        String defaultValue, boolean nullable,
                                                        ModelParameterIfNullBehavior ifNullBehavior,
                                                        String description,
                                                        ModelParameterCapabilityStatus status,
                                                        Instant createdAt, Instant updatedAt) {
        return new ModelParameterCapability(id, modelId, parameterName, apiParameterKey,
                supportStatus, valueType, minValue, maxValue, defaultValue, nullable,
                ifNullBehavior, description, status, createdAt, updatedAt);
    }

    public void update(String apiParameterKey, ModelParameterSupportStatus supportStatus,
                       ModelParameterValueType valueType, BigDecimal minValue, BigDecimal maxValue,
                       String defaultValue, boolean nullable,
                       ModelParameterIfNullBehavior ifNullBehavior, String description) {
        validateApiParameterKey(apiParameterKey, supportStatus);
        validateMinMax(valueType, minValue, maxValue);
        validateNullableBehavior(nullable, ifNullBehavior);
        this.apiParameterKey = apiParameterKey;
        this.supportStatus = supportStatus;
        this.valueType = valueType;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.nullable = nullable;
        this.ifNullBehavior = ifNullBehavior;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.status = ModelParameterCapabilityStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = ModelParameterCapabilityStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    private static void validateModelId(UUID modelId) {
        if (modelId == null) {
            throw new IllegalArgumentException("Model ID is required");
        }
    }

    private static void validateApiParameterKey(String apiParameterKey, ModelParameterSupportStatus supportStatus) {
        if (supportStatus != ModelParameterSupportStatus.NO
                && (apiParameterKey == null || apiParameterKey.isBlank())) {
            throw new IllegalArgumentException(
                    "API parameter key is required when support status is " + supportStatus);
        }
    }

    private static void validateMinMax(ModelParameterValueType valueType, BigDecimal minValue, BigDecimal maxValue) {
        if (valueType == ModelParameterValueType.BOOLEAN || valueType == ModelParameterValueType.STRING) {
            if (minValue != null || maxValue != null) {
                throw new IllegalArgumentException(
                        "minValue and maxValue must be null for " + valueType + " parameters");
            }
        }
        if (minValue != null && maxValue != null && minValue.compareTo(maxValue) > 0) {
            throw new IllegalArgumentException(
                    "minValue must be less than or equal to maxValue, got: " + minValue + " > " + maxValue);
        }
    }

    private static void validateNullableBehavior(boolean nullable, ModelParameterIfNullBehavior ifNullBehavior) {
        if (nullable && ifNullBehavior == null) {
            throw new IllegalArgumentException("ifNullBehavior is required when nullable is true");
        }
    }

    public UUID id() { return id; }
    public UUID modelId() { return modelId; }
    public ModelParameterName parameterName() { return parameterName; }
    public String apiParameterKey() { return apiParameterKey; }
    public ModelParameterSupportStatus supportStatus() { return supportStatus; }
    public ModelParameterValueType valueType() { return valueType; }
    public BigDecimal minValue() { return minValue; }
    public BigDecimal maxValue() { return maxValue; }
    public String defaultValue() { return defaultValue; }
    public boolean nullable() { return nullable; }
    public ModelParameterIfNullBehavior ifNullBehavior() { return ifNullBehavior; }
    public String description() { return description; }
    public ModelParameterCapabilityStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
