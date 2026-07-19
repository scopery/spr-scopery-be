package com.company.scopery.modules.externalparty.stakeholder.domain.model;
import com.company.scopery.modules.externalparty.stakeholder.domain.enums.StakeholderStatus;
import java.time.Instant; import java.util.UUID;
public record ProjectStakeholder(UUID id, UUID projectId, UUID workspaceId, UUID contactId, UUID organizationId,
        UUID internalUserId, String stakeholderRole, String influence, String interest, StakeholderStatus status,
        boolean clientFacing, Instant archivedAt, int version, Instant createdAt, Instant updatedAt) {
    public static ProjectStakeholder create(UUID projectId, UUID workspaceId, UUID contactId, UUID organizationId,
                                            UUID internalUserId, String role, boolean clientFacing) {
        Instant now = Instant.now();
        return new ProjectStakeholder(UUID.randomUUID(), projectId, workspaceId, contactId, organizationId, internalUserId,
                role, null, null, StakeholderStatus.ACTIVE, clientFacing, null, 0, now, now);
    }
}
