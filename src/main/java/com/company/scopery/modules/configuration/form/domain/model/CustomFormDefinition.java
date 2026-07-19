package com.company.scopery.modules.configuration.form.domain.model;
import java.time.Instant; import java.util.UUID;
public record CustomFormDefinition(UUID id, UUID workspaceId, UUID projectId, String objectTypeCode, String formCode, String name,
        String formType, String status, UUID currentVersionId, int version, Instant createdAt, Instant updatedAt) {
    public static CustomFormDefinition create(UUID workspaceId, UUID projectId, String objectType, String formCode, String name, String formType) {
        Instant now = Instant.now();
        return new CustomFormDefinition(UUID.randomUUID(), workspaceId, projectId, objectType, formCode, name, formType, "DRAFT", null, 0, now, now);
    }
    public CustomFormDefinition withCurrentVersion(UUID versionId) {
        return new CustomFormDefinition(id, workspaceId, projectId, objectTypeCode, formCode, name, formType, status, versionId, version, createdAt, Instant.now());
    }
    public CustomFormDefinition archive() {
        return new CustomFormDefinition(id, workspaceId, projectId, objectTypeCode, formCode, name, formType, "ARCHIVED", currentVersionId, version, createdAt, Instant.now());
    }
}
