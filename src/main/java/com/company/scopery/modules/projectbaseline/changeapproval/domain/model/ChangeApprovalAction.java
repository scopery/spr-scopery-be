package com.company.scopery.modules.projectbaseline.changeapproval.domain.model;

import com.company.scopery.modules.projectbaseline.changeapproval.domain.enums.ChangeApprovalActionType;

import java.time.Instant;
import java.util.UUID;

public record ChangeApprovalAction(
        UUID id,
        UUID changeRequestId,
        ChangeApprovalActionType action,
        UUID actorUserId,
        String reason,
        Instant createdAt,
        String traceId
) {
    public static ChangeApprovalAction record(UUID changeRequestId, ChangeApprovalActionType action,
                                              UUID actorUserId, String reason, String traceId) {
        return new ChangeApprovalAction(UUID.randomUUID(), changeRequestId, action, actorUserId,
                reason, Instant.now(), traceId);
    }
}
