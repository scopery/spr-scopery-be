package com.company.scopery.modules.workspace.orgmember.infrastructure.mapper;

import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMemberStatus;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.infrastructure.persistence.OrgMemberJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrgMemberPersistenceMapper {

    public OrgMember toDomain(OrgMemberJpaEntity entity) {
        return new OrgMember(
                entity.getId(),
                entity.getOrganizationId(),
                entity.getUserId(),
                OrgMembershipType.valueOf(entity.getMembershipType()),
                OrgMemberStatus.valueOf(entity.getStatus()),
                OrgMembershipSource.valueOf(entity.getSource()),
                entity.getJoinedAt(),
                entity.getSuspendedAt(),
                entity.getRemovedAt(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public OrgMemberJpaEntity toJpaEntity(OrgMember domain) {
        OrgMemberJpaEntity entity = new OrgMemberJpaEntity();
        entity.setId(domain.id());
        entity.setOrganizationId(domain.organizationId());
        entity.setUserId(domain.userId());
        entity.setMembershipType(domain.membershipType().name());
        entity.setStatus(domain.status().name());
        entity.setSource(domain.source().name());
        entity.setJoinedAt(domain.joinedAt());
        entity.setSuspendedAt(domain.suspendedAt());
        entity.setRemovedAt(domain.removedAt());
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
