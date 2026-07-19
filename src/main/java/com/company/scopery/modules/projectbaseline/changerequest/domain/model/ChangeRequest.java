package com.company.scopery.modules.projectbaseline.changerequest.domain.model;

import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangePriority;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeRequestStatus;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeType;

import java.time.Instant;
import java.util.UUID;

public record ChangeRequest(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID baselineId,
        String code,
        String title,
        String description,
        ChangeType changeType,
        ChangePriority priority,
        ChangeRequestStatus status,
        String reason,
        UUID requestedBy,
        Instant requestedAt,
        Instant submittedAt,
        UUID submittedBy,
        Instant approvedAt,
        UUID approvedBy,
        Instant rejectedAt,
        UUID rejectedBy,
        String rejectionReason,
        Instant cancelledAt,
        UUID cancelledBy,
        Instant appliedAt,
        UUID appliedBy,
        Instant archivedAt,
        UUID archivedBy,
        String traceId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ChangeRequest create(
            UUID projectId, UUID workspaceId, UUID baselineId, String code, String title,
            String description, ChangeType changeType, ChangePriority priority, String reason,
            UUID requestedBy, String traceId) {
        Instant now = Instant.now();
        return new ChangeRequest(
                UUID.randomUUID(), projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, ChangeRequestStatus.DRAFT, reason, requestedBy, now,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                traceId, 0, null, null);
    }

    public boolean isDraft() { return status == ChangeRequestStatus.DRAFT; }
    public boolean isEditable() { return status == ChangeRequestStatus.DRAFT; }

    public ChangeRequest updateDraft(String title, String description, ChangeType changeType,
                                     ChangePriority priority, String reason, UUID baselineId) {
        return new ChangeRequest(id, projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, status, reason, requestedBy, requestedAt, submittedAt, submittedBy,
                approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason, cancelledAt, cancelledBy,
                appliedAt, appliedBy, archivedAt, archivedBy, traceId, version, createdAt, updatedAt);
    }

    public ChangeRequest submit(UUID actorId) {
        return new ChangeRequest(id, projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, ChangeRequestStatus.SUBMITTED, reason, requestedBy, requestedAt,
                Instant.now(), actorId, approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason,
                cancelledAt, cancelledBy, appliedAt, appliedBy, archivedAt, archivedBy, traceId, version,
                createdAt, updatedAt);
    }

    public ChangeRequest approve(UUID actorId) {
        return new ChangeRequest(id, projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, ChangeRequestStatus.APPROVED, reason, requestedBy, requestedAt,
                submittedAt, submittedBy, Instant.now(), actorId, rejectedAt, rejectedBy, rejectionReason,
                cancelledAt, cancelledBy, appliedAt, appliedBy, archivedAt, archivedBy, traceId, version,
                createdAt, updatedAt);
    }

    public ChangeRequest reject(UUID actorId, String rejectionReason) {
        return new ChangeRequest(id, projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, ChangeRequestStatus.REJECTED, reason, requestedBy, requestedAt,
                submittedAt, submittedBy, approvedAt, approvedBy, Instant.now(), actorId, rejectionReason,
                cancelledAt, cancelledBy, appliedAt, appliedBy, archivedAt, archivedBy, traceId, version,
                createdAt, updatedAt);
    }

    public ChangeRequest cancel(UUID actorId) {
        return new ChangeRequest(id, projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, ChangeRequestStatus.CANCELLED, reason, requestedBy, requestedAt,
                submittedAt, submittedBy, approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason,
                Instant.now(), actorId, appliedAt, appliedBy, archivedAt, archivedBy, traceId, version,
                createdAt, updatedAt);
    }

    public ChangeRequest apply(UUID actorId) {
        return new ChangeRequest(id, projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, ChangeRequestStatus.APPLIED, reason, requestedBy, requestedAt,
                submittedAt, submittedBy, approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason,
                cancelledAt, cancelledBy, Instant.now(), actorId, archivedAt, archivedBy, traceId, version,
                createdAt, updatedAt);
    }

    public ChangeRequest archive(UUID actorId) {
        return new ChangeRequest(id, projectId, workspaceId, baselineId, code, title, description,
                changeType, priority, ChangeRequestStatus.ARCHIVED, reason, requestedBy, requestedAt,
                submittedAt, submittedBy, approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason,
                cancelledAt, cancelledBy, appliedAt, appliedBy, Instant.now(), actorId, traceId, version,
                createdAt, updatedAt);
    }
}
