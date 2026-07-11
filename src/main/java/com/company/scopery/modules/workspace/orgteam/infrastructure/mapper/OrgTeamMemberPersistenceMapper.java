package com.company.scopery.modules.workspace.orgteam.infrastructure.mapper;

import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMember;
import com.company.scopery.modules.workspace.orgteam.infrastructure.persistence.OrgTeamMemberJpaEntity;
import com.company.scopery.modules.workspace.orgteam.infrastructure.persistence.OrgTeamMemberKey;
import org.springframework.stereotype.Component;

@Component
public class OrgTeamMemberPersistenceMapper {

    public OrgTeamMember toDomain(OrgTeamMemberJpaEntity entity) {
        return new OrgTeamMember(
                entity.getId().getTeamId(),
                entity.getId().getUserId(),
                entity.getJoinedAt(),
                entity.getCreatedAt());
    }

    public OrgTeamMemberJpaEntity toJpaEntity(OrgTeamMember domain) {
        OrgTeamMemberJpaEntity entity = new OrgTeamMemberJpaEntity();
        entity.setId(new OrgTeamMemberKey(domain.teamId(), domain.userId()));
        entity.setJoinedAt(domain.joinedAt());
        entity.setCreatedAt(domain.createdAt());
        entity.setCreatedBy("SYSTEM");
        return entity;
    }
}
