package com.company.scopery.modules.workspace.orginvitation.infrastructure.mapper;

import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.infrastructure.persistence.OrgInvitationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrgInvitationPersistenceMapper {

    public OrgInvitation toDomain(OrgInvitationJpaEntity entity) {
        return new OrgInvitation(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getInviteeEmail(),
                entity.getInviteeUserId(),
                OrgMembershipType.valueOf(entity.getMembershipType()),
                OrgInvitationStatus.valueOf(entity.getStatus()),
                entity.getInvitedBy(),
                entity.getToken(),
                entity.getExpiresAt(),
                entity.getRespondedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public OrgInvitationJpaEntity toJpaEntity(OrgInvitation domain) {
        OrgInvitationJpaEntity entity = new OrgInvitationJpaEntity();
        entity.setId(domain.id());
        entity.setOrganizationId(domain.organizationId());
        entity.setInviteeEmail(domain.inviteeEmail());
        entity.setInviteeUserId(domain.inviteeUserId());
        entity.setMembershipType(domain.membershipType().name());
        entity.setStatus(domain.status().name());
        entity.setInvitedBy(domain.invitedBy());
        entity.setToken(domain.token());
        entity.setExpiresAt(domain.expiresAt());
        entity.setRespondedAt(domain.respondedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
