package com.company.scopery.modules.ratecard.membercostrole.domain.model;

import com.company.scopery.modules.ratecard.membercostrole.domain.enums.MemberCostRoleStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WorkspaceMemberCostRoleAssignment(
        UUID id, UUID workspaceId, UUID workspaceMemberId, UUID userId, UUID costRoleId,
        boolean isDefault, LocalDate effectiveFrom, LocalDate effectiveTo, MemberCostRoleStatus status,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt
) {
    public static WorkspaceMemberCostRoleAssignment create(UUID workspaceId, UUID workspaceMemberId, UUID userId,
                                                           UUID costRoleId, boolean isDefault,
                                                           LocalDate effectiveFrom, LocalDate effectiveTo) {
        return new WorkspaceMemberCostRoleAssignment(UUID.randomUUID(), workspaceId, workspaceMemberId, userId,
                costRoleId, isDefault, effectiveFrom, effectiveTo, MemberCostRoleStatus.ACTIVE,
                null, null, 0, null, null);
    }

    public WorkspaceMemberCostRoleAssignment update(UUID costRoleId, boolean isDefault,
                                                    LocalDate effectiveFrom, LocalDate effectiveTo) {
        return new WorkspaceMemberCostRoleAssignment(id, workspaceId, workspaceMemberId, userId, costRoleId,
                isDefault, effectiveFrom, effectiveTo, status, archivedAt, archivedBy, version, createdAt, updatedAt);
    }

    public WorkspaceMemberCostRoleAssignment archive(UUID actorId) {
        return new WorkspaceMemberCostRoleAssignment(id, workspaceId, workspaceMemberId, userId, costRoleId,
                isDefault, effectiveFrom, effectiveTo, MemberCostRoleStatus.ARCHIVED,
                Instant.now(), actorId, version, createdAt, updatedAt);
    }

    public boolean overlaps(LocalDate from, LocalDate to) {
        LocalDate thisTo = effectiveTo == null ? LocalDate.MAX : effectiveTo;
        LocalDate otherTo = to == null ? LocalDate.MAX : to;
        return !from.isAfter(thisTo) && !otherTo.isBefore(effectiveFrom);
    }
}
