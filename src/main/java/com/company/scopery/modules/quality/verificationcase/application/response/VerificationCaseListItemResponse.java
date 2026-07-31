package com.company.scopery.modules.quality.verificationcase.application.response;
import java.time.Instant; import java.util.UUID;
public record VerificationCaseListItemResponse(
        UUID id, UUID projectId, UUID requirementId,
        String code, String title,
        String verificationMethod, String lifecycleStatus, String automationStatus,
        AssigneeRef assignee,
        int version, Instant createdAt, Instant updatedAt) {
    public record AssigneeRef(UUID id, String displayName) {}
}
