package com.company.scopery.modules.configuration.form.domain.model;
import java.time.Instant; import java.util.UUID;
public record CustomFormField(UUID id, UUID formVersionId, UUID sectionId, String fieldSource, UUID customFieldDefinitionId,
        String coreFieldKey, String labelOverride, boolean requiredOnForm, boolean readonlyFlag, int sortOrder, String metadataJson,
        int version, Instant createdAt, Instant updatedAt) {
    public static CustomFormField create(UUID versionId, UUID sectionId, String source, UUID customFieldId, String coreKey,
                                         boolean required, boolean readonly, int sortOrder) {
        Instant now = Instant.now();
        return new CustomFormField(UUID.randomUUID(), versionId, sectionId, source, customFieldId, coreKey, null, required, readonly, sortOrder, null, 0, now, now);
    }
}
