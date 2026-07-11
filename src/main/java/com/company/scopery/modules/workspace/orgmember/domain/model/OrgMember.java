package com.company.scopery.modules.workspace.orgmember.domain.model;

import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMemberStatus;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;

import java.time.Instant;
import java.util.UUID;

public record OrgMember(
        UUID id,
        UUID organizationId,
        UUID userId,
        OrgMembershipType membershipType,
        OrgMemberStatus status,
        OrgMembershipSource source,
        Instant joinedAt,
        Instant suspendedAt,
        Instant removedAt,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgMember create(UUID organizationId, UUID userId, OrgMembershipType membershipType) {
        return create(organizationId, userId, membershipType, OrgMembershipSource.MANUAL);
    }

    public static OrgMember create(UUID organizationId, UUID userId, OrgMembershipType membershipType,
                                   OrgMembershipSource source) {
        Instant now = Instant.now();
        return new OrgMember(UUID.randomUUID(), organizationId, userId, membershipType,
                OrgMemberStatus.ACTIVE, source, now, null, null, 0, now, now);
    }

    public OrgMember activate() {
        if (status == OrgMemberStatus.REMOVED) {
            throw new IllegalStateException("Removed organization membership cannot be activated");
        }
        return new OrgMember(id, organizationId, userId, membershipType,
                OrgMemberStatus.ACTIVE, source, joinedAt, null, removedAt, version, createdAt, Instant.now());
    }

    public OrgMember suspend() {
        Instant now = Instant.now();
        return new OrgMember(id, organizationId, userId, membershipType,
                OrgMemberStatus.SUSPENDED, source, joinedAt, now, removedAt, version, createdAt, now);
    }

    public OrgMember remove() {
        Instant now = Instant.now();
        return new OrgMember(id, organizationId, userId, membershipType,
                OrgMemberStatus.REMOVED, source, joinedAt, suspendedAt, now, version, createdAt, now);
    }
}
