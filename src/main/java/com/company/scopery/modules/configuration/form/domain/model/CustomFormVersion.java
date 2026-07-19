package com.company.scopery.modules.configuration.form.domain.model;
import java.time.Instant; import java.util.UUID;
public record CustomFormVersion(UUID id, UUID formDefinitionId, UUID workspaceId, int versionNumber, String status,
        String schemaJson, Instant publishedAt, boolean currentFlag, int version, Instant createdAt, Instant updatedAt) {
    public static CustomFormVersion create(UUID formId, UUID workspaceId, int number) {
        Instant now = Instant.now();
        return new CustomFormVersion(UUID.randomUUID(), formId, workspaceId, number, "DRAFT", null, null, false, 0, now, now);
    }
    public CustomFormVersion publish() {
        if (!"DRAFT".equals(status)) throw new IllegalStateException("Only draft can publish");
        Instant now = Instant.now();
        return new CustomFormVersion(id, formDefinitionId, workspaceId, versionNumber, "PUBLISHED", schemaJson, now, true, version, createdAt, now);
    }
    public boolean isPublished() { return "PUBLISHED".equals(status); }
    public boolean isDraft() { return "DRAFT".equals(status); }
}
