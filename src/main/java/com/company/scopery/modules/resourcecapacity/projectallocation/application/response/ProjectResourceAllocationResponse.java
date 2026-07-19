package com.company.scopery.modules.resourcecapacity.projectallocation.application.response;

import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResourceAllocationResponse(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID workspaceMemberId,
        UUID userId,
        BigDecimal allocationPercent,
        String allocationType,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        String notes,
        Instant archivedAt,
        UUID archivedBy,
        int version
) {

    public static ProjectResourceAllocationResponse from(ProjectResourceAllocation allocation) {
        return new ProjectResourceAllocationResponse(
                allocation.id(),
                allocation.workspaceId(),
                allocation.projectId(),
                allocation.workspaceMemberId(),
                allocation.userId(),
                allocation.allocationPercent(),
                allocation.allocationType().name(),
                allocation.status().name(),
                allocation.startDate(),
                allocation.endDate(),
                allocation.notes(),
                allocation.archivedAt(),
                allocation.archivedBy(),
                allocation.version()
        );
    }
}
