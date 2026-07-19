package com.company.scopery.modules.projectbaseline.changerequest.application.response;

import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequest;

import java.time.Instant;
import java.util.UUID;

public record ChangeRequestResponse(
        UUID id, UUID projectId, UUID workspaceId, UUID baselineId, String code, String title, String description,
        String changeType, String priority, String status, String reason,
        Instant submittedAt, UUID submittedBy, Instant approvedAt, UUID approvedBy,
        Instant rejectedAt, UUID rejectedBy, String rejectionReason,
        Instant appliedAt, UUID appliedBy, Instant createdAt, Instant updatedAt
) {
    public static ChangeRequestResponse from(ChangeRequest cr) {
        return new ChangeRequestResponse(
                cr.id(), cr.projectId(), cr.workspaceId(), cr.baselineId(), cr.code(), cr.title(), cr.description(),
                cr.changeType().name(), cr.priority() == null ? null : cr.priority().name(), cr.status().name(),
                cr.reason(), cr.submittedAt(), cr.submittedBy(), cr.approvedAt(), cr.approvedBy(),
                cr.rejectedAt(), cr.rejectedBy(), cr.rejectionReason(), cr.appliedAt(), cr.appliedBy(),
                cr.createdAt(), cr.updatedAt());
    }
}
