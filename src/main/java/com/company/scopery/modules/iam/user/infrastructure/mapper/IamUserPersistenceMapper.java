package com.company.scopery.modules.iam.user.infrastructure.mapper;

import com.company.scopery.modules.iam.user.domain.enums.IamRegistrationSource;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.iam.user.infrastructure.persistence.IamUserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class IamUserPersistenceMapper {

    public IamUser toDomain(IamUserJpaEntity entity) {
        IamRegistrationSource source = entity.getRegistrationSource() == null
                ? IamRegistrationSource.SELF_SIGNUP
                : IamRegistrationSource.valueOf(entity.getRegistrationSource());
        return new IamUser(
                entity.getId(),
                Username.of(entity.getUsername()),
                EmailAddress.of(entity.getEmail()),
                entity.getFullName(),
                entity.getPasswordHash(),
                IamUserStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                source,
                entity.getPasswordChangedAt(),
                entity.isPasswordResetRequired(),
                entity.getFailedLoginCount(),
                entity.getLastLoginAt(),
                entity.getLastLoginFailedAt(),
                entity.isEmailVerified());
    }

    public IamUserJpaEntity toJpaEntity(IamUser domain) {
        IamUserJpaEntity entity = new IamUserJpaEntity();
        entity.setId(domain.id());
        entity.setUsername(domain.username().value());
        entity.setEmail(domain.email().value());
        entity.setFullName(domain.fullName());
        entity.setPasswordHash(domain.passwordHash());
        entity.setStatus(domain.status().name());
        entity.setRegistrationSource(domain.registrationSource().name());
        entity.setPasswordChangedAt(domain.passwordChangedAt());
        entity.setPasswordResetRequired(domain.passwordResetRequired());
        entity.setFailedLoginCount(domain.failedLoginCount());
        entity.setLastLoginAt(domain.lastLoginAt());
        entity.setLastLoginFailedAt(domain.lastLoginFailedAt());
        entity.setEmailVerified(domain.emailVerified());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
