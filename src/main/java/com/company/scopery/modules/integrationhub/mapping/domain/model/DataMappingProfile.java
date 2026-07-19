package com.company.scopery.modules.integrationhub.mapping.domain.model;
import java.time.Instant; import java.util.UUID;
public record DataMappingProfile(UUID id, UUID workspaceId, UUID connectionId, String mappingCode, String name,
        String targetObjectType, String sourceFormat, String mappingJson, String validationRulesJson,
        String status, Instant archivedAt, int version, Instant createdAt, Instant updatedAt) {

    public static DataMappingProfile create(UUID workspaceId, UUID connectionId, String code, String name,
            String targetObjectType, String sourceFormat, String mappingJson) {
        Instant now = Instant.now();
        return new DataMappingProfile(UUID.randomUUID(), workspaceId, connectionId, code, name, targetObjectType,
                sourceFormat, mappingJson, null, "ACTIVE", null, 0, now, now);
    }

    public DataMappingProfile update(String name, String mappingJson, String validationRulesJson) {
        return new DataMappingProfile(id, workspaceId, connectionId, mappingCode, name, targetObjectType,
                sourceFormat, mappingJson, validationRulesJson, status, archivedAt, version, createdAt, Instant.now());
    }

    public DataMappingProfile archive() {
        return new DataMappingProfile(id, workspaceId, connectionId, mappingCode, name, targetObjectType,
                sourceFormat, mappingJson, validationRulesJson, "ARCHIVED", Instant.now(), version, createdAt, Instant.now());
    }
}
