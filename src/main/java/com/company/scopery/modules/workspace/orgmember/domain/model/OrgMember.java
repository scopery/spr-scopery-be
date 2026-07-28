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
        Integer version,
        Instant createdAt,
        Instant updatedAt) {

    public static OrgMember create(UUID organizationId, UUID userId, OrgMembershipType membershipType) {
        return create(organizationId, userId, membershipType, OrgMembershipSource.MANUAL);
    }

    public static OrgMember create(UUID organizationId, UUID userId, OrgMembershipType membershipType,
                                   OrgMembershipSource source) {
        Instant now = Instant.now();
        return new OrgMember(UUID.randomUUID(), organizationId, userId, membershipType,
                OrgMemberStatus.ACTIVE, source, now, null, null, null, now, now);
    }

    public OrgMember activate() {
        Instant now = Instant.now();
        // Kick/rejoin: REMOVED and SUSPENDED can return to ACTIVE (unique org+user row is retained).
        return new OrgMember(id, organizationId, userId, membershipType,
                OrgMemberStatus.ACTIVE, source, joinedAt != null ? joinedAt : now, null, null, version, createdAt, now);
    }

    /** Rejoin after kick with an updated membership type (e.g. from invitation). */
    public OrgMember reinstate(OrgMembershipType newMembershipType, OrgMembershipSource newSource) {
        Instant now = Instant.now();
        return new OrgMember(id, organizationId, userId,
                newMembershipType != null ? newMembershipType : membershipType,
                OrgMemberStatus.ACTIVE,
                newSource != null ? newSource : source,
                joinedAt != null ? joinedAt : now, null, null, version, createdAt, now);
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
