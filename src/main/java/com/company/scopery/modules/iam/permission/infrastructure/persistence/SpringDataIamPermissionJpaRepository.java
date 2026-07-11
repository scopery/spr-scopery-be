package com.company.scopery.modules.iam.permission.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataIamPermissionJpaRepository
        extends JpaRepository<IamPermissionJpaEntity, UUID>, JpaSpecificationExecutor<IamPermissionJpaEntity> {

    boolean existsByCode(String code);

    Optional<IamPermissionJpaEntity> findByCode(String code);
}
