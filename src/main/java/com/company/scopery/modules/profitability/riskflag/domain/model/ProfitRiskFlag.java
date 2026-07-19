package com.company.scopery.modules.profitability.riskflag.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProfitRiskFlag(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        String reason,
        String impactType,
        BigDecimal amountAtRisk,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProfitRiskFlag create(
            UUID workspaceId,
            UUID projectId,
            String reason,
            String impactType,
            BigDecimal amountAtRisk) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }
        if (impactType == null || impactType.isBlank()) {
            throw new IllegalArgumentException("Impact type is required");
        }
        Instant now = Instant.now();
        return new ProfitRiskFlag(
                UUID.randomUUID(),
                workspaceId,
                projectId,
                reason.trim(),
                impactType.trim(),
                amountAtRisk,
                "OPEN",
                0,
                now,
                now);
    }

    public ProfitRiskFlag mitigate() {
        if (!"OPEN".equals(status)) {
            throw new IllegalStateException("Risk flag can only be mitigated when OPEN, current status: " + status);
        }
        return new ProfitRiskFlag(id, workspaceId, projectId, reason, impactType, amountAtRisk,
                "MITIGATED", version, createdAt, Instant.now());
    }

    public ProfitRiskFlag close() {
        if ("CLOSED".equals(status)) {
            throw new IllegalStateException("Risk flag is already closed");
        }
        return new ProfitRiskFlag(id, workspaceId, projectId, reason, impactType, amountAtRisk,
                "CLOSED", version, createdAt, Instant.now());
    }
}
