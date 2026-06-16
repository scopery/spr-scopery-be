package com.company.scopery.modules.iam.user.infrastructure.mapper;

import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.iam.user.infrastructure.persistence.IamUserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamUserPersistenceMapper {

    public IamUser toDomain(IamUserJpaEntity entity) {
        return new IamUser(
                entity.getId(),
                Username.of(entity.getUsername()),
                EmailAddress.of(entity.getEmail()),
                entity.getFullName(),
                entity.getPasswordHash(),
                IamUserStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public IamUserJpaEntity toJpaEntity(IamUser domain) {
        IamUserJpaEntity entity = new IamUserJpaEntity();
        entity.setId(domain.id());
        entity.setUsername(domain.username().value());
        entity.setEmail(domain.email().value());
        entity.setFullName(domain.fullName());
        entity.setPasswordHash(domain.passwordHash());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
