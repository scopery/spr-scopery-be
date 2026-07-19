package com.company.scopery.modules.integrationhub.exportprofile.domain.model;
import java.time.Instant; import java.util.UUID;
public record ExportProfile(UUID id, UUID workspaceId, UUID connectionId, String profileCode, String name,
        String exportFormat, String targetDestination, String objectScope, String columnsJson, String filtersJson,
        String maskingPolicy, String status, Instant archivedAt, int version, Instant createdAt, Instant updatedAt) {

    public static ExportProfile create(UUID workspaceId, UUID connectionId, String code, String name,
            String format, String destination, String scope) {
        Instant now = Instant.now();
        return new ExportProfile(UUID.randomUUID(), workspaceId, connectionId, code, name, format, destination, scope,
                null, null, null, "ACTIVE", null, 0, now, now);
    }

    public ExportProfile update(String name, String columnsJson, String filtersJson, String maskingPolicy) {
        return new ExportProfile(id, workspaceId, connectionId, profileCode, name, exportFormat, targetDestination,
                objectScope, columnsJson, filtersJson, maskingPolicy, status, archivedAt, version, createdAt, Instant.now());
    }

    public ExportProfile archive() {
        return new ExportProfile(id, workspaceId, connectionId, profileCode, name, exportFormat, targetDestination,
                objectScope, columnsJson, filtersJson, maskingPolicy, "ARCHIVED", Instant.now(), version, createdAt, Instant.now());
    }
}
