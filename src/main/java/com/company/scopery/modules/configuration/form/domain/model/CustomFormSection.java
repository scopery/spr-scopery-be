package com.company.scopery.modules.configuration.form.domain.model;
import java.time.Instant; import java.util.UUID;
public record CustomFormSection(UUID id, UUID formVersionId, String title, int sortOrder, String visibilityJson,
        int version, Instant createdAt, Instant updatedAt) {
    public static CustomFormSection create(UUID versionId, String title, int sortOrder) {
        Instant now = Instant.now();
        return new CustomFormSection(UUID.randomUUID(), versionId, title, sortOrder, null, 0, now, now);
    }
}
