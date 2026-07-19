package com.company.scopery.modules.configuration.form.domain.model;
import java.time.Instant; import java.util.UUID;
public record FormSubmission(UUID id, UUID workspaceId, UUID projectId, UUID formDefinitionId, UUID formVersionId,
        String objectTypeCode, UUID targetId, String principalType, UUID submittedBy, String payloadJson,
        String validationStatus, String validationErrorsJson, String status, int version, Instant createdAt, Instant updatedAt) {
    public static FormSubmission accepted(UUID workspaceId, UUID projectId, UUID formId, UUID versionId, String objectType,
                                          UUID targetId, UUID userId, String payload) {
        Instant now = Instant.now();
        return new FormSubmission(UUID.randomUUID(), workspaceId, projectId, formId, versionId, objectType, targetId, "USER", userId,
                payload, "VALID", null, "ACCEPTED", 0, now, now);
    }
    public static FormSubmission rejected(UUID workspaceId, UUID formId, UUID versionId, UUID userId, String payload, String errors) {
        Instant now = Instant.now();
        return new FormSubmission(UUID.randomUUID(), workspaceId, null, formId, versionId, null, null, "USER", userId,
                payload, "INVALID", errors, "REJECTED", 0, now, now);
    }
    public static FormSubmission acceptedPortal(UUID workspaceId, UUID projectId, UUID formId, UUID versionId, String objectType,
                                                UUID targetId, UUID portalAccountId, String payload) {
        Instant now = Instant.now();
        return new FormSubmission(UUID.randomUUID(), workspaceId, projectId, formId, versionId, objectType, targetId, "PORTAL",
                portalAccountId, payload, "VALID", null, "ACCEPTED", 0, now, now);
    }
    public static FormSubmission rejectedPortal(UUID workspaceId, UUID formId, UUID versionId, UUID portalAccountId, String payload, String errors) {
        Instant now = Instant.now();
        return new FormSubmission(UUID.randomUUID(), workspaceId, null, formId, versionId, null, null, "PORTAL", portalAccountId,
                payload, "INVALID", errors, "REJECTED", 0, now, now);
    }
}
