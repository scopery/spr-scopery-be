package com.company.scopery.modules.aiagent.eventconfig.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SpringDataEventConfigJpaRepository
        extends JpaRepository<EventConfigJpaEntity, UUID>,
                JpaSpecificationExecutor<EventConfigJpaEntity> {

    boolean existsByCode(String code);

    List<EventConfigJpaEntity> findByStatus(String status);
}
