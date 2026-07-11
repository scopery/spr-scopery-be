package com.company.scopery.modules.aiagent.providersecret.domain.model;

import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretStatus;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;

import java.time.Instant;
import java.util.UUID;

public class ProviderSecret {

    private final UUID id;
    private final UUID providerId;
    private final ProviderSecretType secretType;
    private final String encryptedValue;
    private final String iv;
    private final String keyVersion;
    private final String maskedValue;
    private String description;
    private ProviderSecretStatus status;
    private Instant lastRotatedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private ProviderSecret(UUID id, UUID providerId, ProviderSecretType secretType,
                           String encryptedValue, String iv, String keyVersion,
                           String maskedValue, String description, ProviderSecretStatus status,
                           Instant lastRotatedAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.secretType = secretType;
        this.encryptedValue = encryptedValue;
        this.iv = iv;
        this.keyVersion = keyVersion;
        this.maskedValue = maskedValue;
        this.description = description;
        this.status = status;
        this.lastRotatedAt = lastRotatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProviderSecret create(UUID providerId, ProviderSecretType secretType,
                                        String encryptedValue, String iv, String keyVersion,
                                        String maskedValue, String description) {
        Instant now = Instant.now();
        return new ProviderSecret(UUID.randomUUID(), providerId, secretType,
                encryptedValue, iv, keyVersion, maskedValue, description,
                ProviderSecretStatus.ACTIVE, now, now, now);
    }

    public static ProviderSecret reconstitute(UUID id, UUID providerId, ProviderSecretType secretType,
                                              String encryptedValue, String iv, String keyVersion,
                                              String maskedValue, String description,
                                              ProviderSecretStatus status, Instant lastRotatedAt,
                                              Instant createdAt, Instant updatedAt) {
        return new ProviderSecret(id, providerId, secretType, encryptedValue, iv, keyVersion,
                maskedValue, description, status, lastRotatedAt, createdAt, updatedAt);
    }

    public void deactivate() {
        this.status = ProviderSecretStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID providerId() { return providerId; }
    public ProviderSecretType secretType() { return secretType; }
    public String encryptedValue() { return encryptedValue; }
    public String iv() { return iv; }
    public String keyVersion() { return keyVersion; }
    public String maskedValue() { return maskedValue; }
    public String description() { return description; }
    public ProviderSecretStatus status() { return status; }
    public Instant lastRotatedAt() { return lastRotatedAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
