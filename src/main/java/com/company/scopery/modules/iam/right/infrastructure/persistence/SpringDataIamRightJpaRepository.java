package com.company.scopery.modules.iam.right.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataIamRightJpaRepository
        extends JpaRepository<IamRightJpaEntity, UUID>, JpaSpecificationExecutor<IamRightJpaEntity> {

    boolean existsByCode(String code);
    Optional<IamRightJpaEntity> findByCode(String code);
}
