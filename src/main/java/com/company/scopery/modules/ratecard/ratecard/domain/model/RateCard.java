package com.company.scopery.modules.ratecard.ratecard.domain.model;

import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardScope;
import com.company.scopery.modules.ratecard.ratecard.domain.enums.RateCardStatus;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public record RateCard(
        UUID id, String code, String name, String description, RateCardScope scope,
        UUID organizationId, UUID workspaceId, UUID clientId, UUID projectId,
        String defaultCurrencyCode, boolean isDefault, RateCardStatus status,
        UUID currentVersionId, boolean builtIn,
        Instant archivedAt, UUID archivedBy, int version,
        Instant createdAt, Instant updatedAt
) {
    public static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase(Locale.ROOT);
    }

    public static RateCard create(String code, String name, String description, RateCardScope scope,
                                  UUID organizationId, UUID workspaceId, UUID clientId, UUID projectId,
                                  String defaultCurrencyCode, boolean isDefault) {
        validateScopeKeys(scope, organizationId, workspaceId, clientId, projectId);
        return new RateCard(
                UUID.randomUUID(), normalizeCode(code), name, description, scope,
                organizationId, workspaceId, clientId, projectId,
                defaultCurrencyCode, isDefault, RateCardStatus.DRAFT,
                null, false, null, null, 0, null, null
        );
    }

    private static void validateScopeKeys(
            RateCardScope scope,
            UUID organizationId,
            UUID workspaceId,
            UUID clientId,
            UUID projectId) {
        switch (scope) {
            case SYSTEM -> {
                if (organizationId != null || workspaceId != null || clientId != null || projectId != null) {
                    throw new IllegalArgumentException("SYSTEM scope must not include org/workspace/client/project ids");
                }
            }
            case ORGANIZATION -> {
                if (organizationId == null) {
                    throw new IllegalArgumentException("ORGANIZATION scope requires organizationId");
                }
                if (workspaceId != null || clientId != null || projectId != null) {
                    throw new IllegalArgumentException("ORGANIZATION scope must not include workspace/client/project ids");
                }
            }
            case WORKSPACE -> {
                if (workspaceId == null) {
                    throw new IllegalArgumentException("WORKSPACE scope requires workspaceId");
                }
                if (clientId != null || projectId != null) {
                    throw new IllegalArgumentException("WORKSPACE scope must not include client/project ids");
                }
            }
            case CLIENT -> {
                if (clientId == null) {
                    throw new IllegalArgumentException("CLIENT scope requires clientId");
                }
                if (projectId != null) {
                    throw new IllegalArgumentException("CLIENT scope must not include projectId");
                }
            }
            case PROJECT -> {
                if (projectId == null) {
                    throw new IllegalArgumentException("PROJECT scope requires projectId");
                }
            }
        }
    }

    /** @deprecated use {@link #create(String, String, String, RateCardScope, UUID, UUID, UUID, UUID, String, boolean)} */
    @Deprecated
    public static RateCard create(String code, String name, String description, RateCardScope scope,
                                  UUID organizationId, UUID workspaceId, String defaultCurrencyCode,
                                  boolean isDefault) {
        return create(code, name, description, scope, organizationId, workspaceId, null, null, defaultCurrencyCode, isDefault);
    }

    public RateCard update(String name, String description, String defaultCurrencyCode) {
        return new RateCard(id, code, name, description, scope, organizationId, workspaceId,
                clientId, projectId, defaultCurrencyCode, isDefault, status, currentVersionId,
                builtIn, archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public RateCard activate() {
        return withStatus(RateCardStatus.ACTIVE, archivedAt, archivedBy, currentVersionId, isDefault);
    }

    public RateCard deactivate() {
        return withStatus(RateCardStatus.INACTIVE, archivedAt, archivedBy, currentVersionId, isDefault);
    }

    public RateCard archive(UUID actorId) {
        return withStatus(RateCardStatus.ARCHIVED, Instant.now(), actorId, currentVersionId, false);
    }

    public RateCard withCurrentVersion(UUID versionId) {
        RateCardStatus newStatus = status == RateCardStatus.DRAFT ? RateCardStatus.ACTIVE : status;
        return withStatus(newStatus, archivedAt, archivedBy, versionId, isDefault);
    }

    public RateCard setDefault(boolean value) {
        return withStatus(status, archivedAt, archivedBy, currentVersionId, value);
    }

    private RateCard withStatus(RateCardStatus newStatus, Instant archivedAt, UUID archivedBy,
                                UUID currentVersionId, boolean isDefault) {
        return new RateCard(id, code, name, description, scope, organizationId, workspaceId,
                clientId, projectId, defaultCurrencyCode, isDefault, newStatus, currentVersionId,
                builtIn, archivedAt, archivedBy, version, createdAt, updatedAt);
    }
}
