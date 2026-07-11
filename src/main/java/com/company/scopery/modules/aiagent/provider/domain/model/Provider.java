package com.company.scopery.modules.aiagent.provider.domain.model;

import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;

import java.time.Instant;
import java.util.UUID;

public class Provider {

    private final UUID id;
    private String name;
    private final ProviderCode code;
    private ProviderType type;
    private String apiBaseUrl;
    private String description;
    private ProviderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Provider(UUID id, String name, ProviderCode code, ProviderType type,
                     String apiBaseUrl, String description, ProviderStatus status,
                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
        this.apiBaseUrl = apiBaseUrl;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Provider create(String name, ProviderCode code, ProviderType type,
                                  String apiBaseUrl, String description) {
        validateName(name);
        validateType(type);
        validateApiBaseUrlForActive(apiBaseUrl);
        Instant now = Instant.now();
        return new Provider(UUID.randomUUID(), name, code, type, apiBaseUrl, description,
                            ProviderStatus.ACTIVE, now, now);
    }

    // Restores a Provider from persistence — skips creation rules, trusts stored data.
    public static Provider reconstitute(UUID id, String name, ProviderCode code, ProviderType type,
                                        String apiBaseUrl, String description, ProviderStatus status,
                                        Instant createdAt, Instant updatedAt) {
        return new Provider(id, name, code, type, apiBaseUrl, description, status, createdAt, updatedAt);
    }

    public void update(String name, ProviderType type, String apiBaseUrl, String description) {
        validateName(name);
        validateType(type);
        if (this.status == ProviderStatus.ACTIVE) {
            validateApiBaseUrlForActive(apiBaseUrl);
        }
        this.name = name;
        this.type = type;
        this.apiBaseUrl = apiBaseUrl;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == ProviderStatus.DEPRECATED) {
            throw new IllegalStateException("DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED");
        }
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            throw new IllegalStateException("ACTIVE_PROVIDER_REQUIRES_API_BASE_URL");
        }
        this.status = ProviderStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = ProviderStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Provider name is required");
        }
    }

    private static void validateType(ProviderType type) {
        if (type == null) {
            throw new IllegalArgumentException("Provider type is required");
        }
    }

    private static void validateApiBaseUrlForActive(String apiBaseUrl) {
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            throw new IllegalArgumentException("Active provider must have an API base URL");
        }
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public ProviderCode code() { return code; }
    public ProviderType type() { return type; }
    public String apiBaseUrl() { return apiBaseUrl; }
    public String description() { return description; }
    public ProviderStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
