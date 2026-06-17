package com.company.scopery.modules.iam.role.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataIamRoleJpaRepository
        extends JpaRepository<IamRoleJpaEntity, UUID>, JpaSpecificationExecutor<IamRoleJpaEntity> {

    Optional<IamRoleJpaEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndWorkspaceId(String code, UUID workspaceId);
}
