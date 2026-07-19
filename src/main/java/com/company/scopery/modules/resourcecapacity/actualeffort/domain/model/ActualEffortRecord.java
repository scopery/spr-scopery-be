package com.company.scopery.modules.resourcecapacity.actualeffort.domain.model;
import com.company.scopery.modules.resourcecapacity.actualeffort.domain.enums.*;
import java.math.BigDecimal; import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record ActualEffortRecord(UUID id, UUID workspaceId, UUID projectId, UUID resourceProfileId, String targetType, UUID targetId,
        LocalDate effortDate, BigDecimal effortHours, ActualEffortInputMode inputMode, String description, ActualEffortStatus status,
        Instant cancelledAt, UUID cancelledBy, String cancellationReason, int version, Instant createdAt, Instant updatedAt) {
    public static ActualEffortRecord record(UUID workspaceId, UUID projectId, UUID resourceProfileId, String targetType, UUID targetId,
                                            LocalDate date, BigDecimal hours, ActualEffortInputMode mode, String description) {
        if (hours == null || hours.signum() < 0) throw new IllegalArgumentException("hours");
        Instant now = Instant.now();
        return new ActualEffortRecord(UUID.randomUUID(), workspaceId, projectId, resourceProfileId, targetType, targetId,
                date, hours, mode, description, ActualEffortStatus.RECORDED, null, null, null, 0, now, now);
    }
    public ActualEffortRecord cancel(UUID actor, String reason) {
        if (status == ActualEffortStatus.CANCELLED) throw new IllegalStateException("already cancelled");
        return new ActualEffortRecord(id, workspaceId, projectId, resourceProfileId, targetType, targetId, effortDate, effortHours,
                inputMode, description, ActualEffortStatus.CANCELLED, Instant.now(), actor, reason, version, createdAt, Instant.now());
    }
}
