package com.company.scopery.modules.quality.verificationcase.domain.model;
import java.time.Instant; import java.util.UUID;
public record VerificationCaseListRow(
        UUID id, UUID projectId, UUID requirementId,
        String code, String title,
        String verificationMethod, String lifecycleStatus, String automationStatus,
        UUID assigneeId, String assigneeDisplayName,
        int version, Instant createdAt, Instant updatedAt) {}
