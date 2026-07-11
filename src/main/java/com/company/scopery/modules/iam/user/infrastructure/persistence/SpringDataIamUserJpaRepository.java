package com.company.scopery.modules.iam.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataIamUserJpaRepository
        extends JpaRepository<IamUserJpaEntity, UUID>, JpaSpecificationExecutor<IamUserJpaEntity> {

    Optional<IamUserJpaEntity> findByUsername(String username);
    Optional<IamUserJpaEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
