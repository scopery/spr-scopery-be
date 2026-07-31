package com.company.scopery.modules.quality.verificationcase.domain.model;
import com.company.scopery.modules.quality.verificationcase.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record VerificationCase(
        UUID id, UUID projectId, UUID requirementId,
        String code, String title, String description,
        VerificationMethod verificationMethod,
        String procedure, String expectedResultJson,
        String environment,
        VerificationCaseStatus lifecycleStatus,
        VerificationAutomationStatus automationStatus,
        UUID ownerId, UUID assigneeId,
        Instant archivedAt, UUID archivedBy,
        int version, Instant createdAt, Instant updatedAt) {

    public static VerificationCase create(UUID projectId, UUID requirementId, String code, String title,
            String description, VerificationMethod verificationMethod, String procedure,
            String expectedResultJson, String environment,
            VerificationAutomationStatus automationStatus, UUID ownerId, UUID assigneeId) {
        Instant now = Instant.now();
        return new VerificationCase(UUID.randomUUID(), projectId, requirementId,
                code, title, description, verificationMethod,
                procedure, expectedResultJson, environment,
                VerificationCaseStatus.DRAFT,
                automationStatus != null ? automationStatus : VerificationAutomationStatus.MANUAL,
                ownerId, assigneeId, null, null,
                -1, now, now);
    }

    public VerificationCase update(String title, String description, VerificationMethod verificationMethod,
            String procedure, String expectedResultJson, String environment,
            VerificationCaseStatus lifecycleStatus, VerificationAutomationStatus automationStatus,
            UUID ownerId, UUID assigneeId) {
        return new VerificationCase(id, projectId, requirementId, code,
                title != null ? title : this.title,
                description != null ? description : this.description,
                verificationMethod != null ? verificationMethod : this.verificationMethod,
                procedure != null ? procedure : this.procedure,
                expectedResultJson != null ? expectedResultJson : this.expectedResultJson,
                environment != null ? environment : this.environment,
                lifecycleStatus != null ? lifecycleStatus : this.lifecycleStatus,
                automationStatus != null ? automationStatus : this.automationStatus,
                ownerId != null ? ownerId : this.ownerId,
                assigneeId != null ? assigneeId : this.assigneeId,
                archivedAt, archivedBy, version, createdAt, Instant.now());
    }

    public VerificationCase archive(UUID actorId) {
        if (lifecycleStatus == VerificationCaseStatus.ARCHIVED) throw new IllegalStateException("already archived");
        return new VerificationCase(id, projectId, requirementId, code, title, description,
                verificationMethod, procedure, expectedResultJson, environment,
                VerificationCaseStatus.ARCHIVED, automationStatus,
                ownerId, assigneeId, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
