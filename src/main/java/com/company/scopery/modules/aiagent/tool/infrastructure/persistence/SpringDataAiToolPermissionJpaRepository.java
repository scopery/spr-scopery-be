package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolPermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAiToolPermissionJpaRepository extends JpaRepository<AiToolPermissionJpaEntity, UUID> {

    boolean existsByToolIdAndPermissionCode(UUID toolId, String permissionCode);

    List<AiToolPermissionJpaEntity> findByToolId(UUID toolId);
}
