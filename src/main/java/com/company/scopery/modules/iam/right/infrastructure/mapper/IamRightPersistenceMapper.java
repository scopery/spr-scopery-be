package com.company.scopery.modules.iam.right.infrastructure.mapper;

import com.company.scopery.modules.iam.right.domain.IamRight;
import com.company.scopery.modules.iam.right.domain.IamRightCode;
import com.company.scopery.modules.iam.right.domain.IamRightStatus;
import com.company.scopery.modules.iam.right.infrastructure.persistence.IamRightJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamRightPersistenceMapper {

    public IamRight toDomain(IamRightJpaEntity entity) {
        return new IamRight(
                entity.getId(),
                IamRightCode.of(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                entity.getModule(),
                IamRightStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public IamRightJpaEntity toJpaEntity(IamRight domain) {
        IamRightJpaEntity entity = new IamRightJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setModule(domain.module());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
