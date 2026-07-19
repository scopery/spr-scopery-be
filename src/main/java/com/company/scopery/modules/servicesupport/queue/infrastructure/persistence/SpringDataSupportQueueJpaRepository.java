package com.company.scopery.modules.servicesupport.queue.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSupportQueueJpaRepository extends JpaRepository<SupportQueueJpaEntity, UUID> {
    List<SupportQueueJpaEntity> findByWorkspaceId(UUID workspaceId);
}
