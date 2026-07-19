package com.company.scopery.modules.integrationhub.importtemplate.domain.model;
import java.time.Instant; import java.util.UUID;
public record ImportTemplate(UUID id, UUID workspaceId, String templateCode, String name,
        String targetObjectType, String sourceFormat, String schemaJson, boolean enabled,
        int version, Instant createdAt, Instant updatedAt) {

    public static ImportTemplate system(String code, String name, String targetObjectType, String sourceFormat, String schemaJson) {
        Instant now = Instant.now();
        return new ImportTemplate(UUID.randomUUID(), null, code, name, targetObjectType, sourceFormat, schemaJson, true, 0, now, now);
    }

    public static ImportTemplate workspace(UUID workspaceId, String code, String name,
            String targetObjectType, String sourceFormat, String schemaJson) {
        Instant now = Instant.now();
        return new ImportTemplate(UUID.randomUUID(), workspaceId, code, name, targetObjectType, sourceFormat, schemaJson, true, 0, now, now);
    }
}
