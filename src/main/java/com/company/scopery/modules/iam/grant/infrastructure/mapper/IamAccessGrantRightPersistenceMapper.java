package com.company.scopery.modules.iam.grant.infrastructure.mapper;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.infrastructure.persistence.IamAccessGrantRightJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamAccessGrantRightPersistenceMapper {

    public IamAccessGrantRight toDomain(IamAccessGrantRightJpaEntity entity) {
        return new IamAccessGrantRight(
                entity.getGrantId(),
                entity.getRightId(),
                entity.getCreatedAt()
        );
    }

    public IamAccessGrantRightJpaEntity toJpaEntity(IamAccessGrantRight domain) {
        IamAccessGrantRightJpaEntity entity = new IamAccessGrantRightJpaEntity();
        entity.setGrantId(domain.grantId());
        entity.setRightId(domain.rightId());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
