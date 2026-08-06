package com.company.scopery.modules.specpack.pack.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataSpecPackJpaRepository
        extends JpaRepository<SpecPackJpaEntity, UUID>, JpaSpecificationExecutor<SpecPackJpaEntity> {
}
