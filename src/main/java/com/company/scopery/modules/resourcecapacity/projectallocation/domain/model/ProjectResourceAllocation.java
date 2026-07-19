package com.company.scopery.modules.resourcecapacity.projectallocation.domain.model;

import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.AllocationType;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.ProjectResourceAllocationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResourceAllocation(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID workspaceMemberId,
        UUID userId,
        BigDecimal allocationPercent,
        AllocationType allocationType,
        ProjectResourceAllocationStatus status,
        LocalDate startDate,
        LocalDate endDate,
        String notes,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectResourceAllocation create(
            UUID workspaceId,
            UUID projectId,
            UUID workspaceMemberId,
            UUID userId,
            BigDecimal allocationPercent,
            AllocationType allocationType,
            LocalDate startDate,
            LocalDate endDate,
            String notes) {
        return new ProjectResourceAllocation(
                UUID.randomUUID(), workspaceId, projectId, workspaceMemberId, userId,
                allocationPercent, allocationType, ProjectResourceAllocationStatus.ACTIVE,
                startDate, endDate, notes, null, null, 0, null, null
        );
    }

    public ProjectResourceAllocation update(
            BigDecimal allocationPercent,
            AllocationType allocationType,
            LocalDate startDate,
            LocalDate endDate,
            String notes) {
        return new ProjectResourceAllocation(
                this.id, this.workspaceId, this.projectId, this.workspaceMemberId, this.userId,
                allocationPercent, allocationType, this.status,
                startDate, endDate, notes, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public ProjectResourceAllocation activate() {
        return withStatus(ProjectResourceAllocationStatus.ACTIVE);
    }

    public ProjectResourceAllocation deactivate() {
        return withStatus(ProjectResourceAllocationStatus.INACTIVE);
    }

    public ProjectResourceAllocation archive(UUID actorId) {
        return new ProjectResourceAllocation(
                this.id, this.workspaceId, this.projectId, this.workspaceMemberId, this.userId,
                this.allocationPercent, this.allocationType, ProjectResourceAllocationStatus.ARCHIVED,
                this.startDate, this.endDate, this.notes, Instant.now(), actorId,
                this.version, this.createdAt, this.updatedAt
        );
    }

    private ProjectResourceAllocation withStatus(ProjectResourceAllocationStatus newStatus) {
        return new ProjectResourceAllocation(
                this.id, this.workspaceId, this.projectId, this.workspaceMemberId, this.userId,
                this.allocationPercent, this.allocationType, newStatus,
                this.startDate, this.endDate, this.notes, this.archivedAt, this.archivedBy,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public boolean overlaps(LocalDate otherFrom, LocalDate otherTo) {
        LocalDate thisEnd = this.endDate != null ? this.endDate : LocalDate.MAX;
        LocalDate otherEnd = otherTo != null ? otherTo : LocalDate.MAX;
        return !this.startDate.isAfter(otherEnd) && !otherFrom.isAfter(thisEnd);
    }
}
