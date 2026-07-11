package com.company.scopery.modules.workspace.orgmember.application.response;

import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;

import java.time.Instant;
import java.util.UUID;

public record OrgMemberResponse(
        UUID id,
        UUID organizationId,
        UUID userId,
        String membershipType,
        String status,
        String source,
        Instant joinedAt,
        Instant suspendedAt,
        Instant removedAt,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgMemberResponse from(OrgMember domain) {
        return new OrgMemberResponse(
                domain.id(),
                domain.organizationId(),
                domain.userId(),
                domain.membershipType().name(),
                domain.status().name(),
                domain.source().name(),
                domain.joinedAt(),
                domain.suspendedAt(),
                domain.removedAt(),
                domain.version(),
                domain.createdAt(),
                domain.updatedAt());
    }
}
