package com.company.scopery.modules.quality.verificationcase.application.response;
import com.company.scopery.modules.quality.verificationcase.domain.model.VerificationCase;
import java.time.Instant; import java.util.UUID;
public record VerificationCaseResponse(
        UUID id, UUID projectId, UUID requirementId,
        String code, String title, String description,
        String verificationMethod, String procedure, String expectedResultJson,
        String environment, String lifecycleStatus, String automationStatus,
        UUID ownerId, UUID assigneeId,
        Instant archivedAt, Instant createdAt, Instant updatedAt, Long version) {
    public static VerificationCaseResponse from(VerificationCase e) {
        return new VerificationCaseResponse(
                e.id(), e.projectId(), e.requirementId(),
                e.code(), e.title(), e.description(),
                e.verificationMethod().name(), e.procedure(), e.expectedResultJson(),
                e.environment(), e.lifecycleStatus().name(),
                e.automationStatus() != null ? e.automationStatus().name() : "MANUAL",
                e.ownerId(), e.assigneeId(),
                e.archivedAt(), e.createdAt(), e.updatedAt(), (long) e.version());
    }
}
