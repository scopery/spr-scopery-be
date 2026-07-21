package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionToolPolicyJpaRepository extends JpaRepository<AiActionToolPolicyJpaEntity, UUID> {

    Optional<AiActionToolPolicyJpaEntity> findByToolCodeAndToolVersion(String toolCode, String toolVersion);

    List<AiActionToolPolicyJpaEntity> findByInvocationScopeAndStatus(String invocationScope, String status);

    List<AiActionToolPolicyJpaEntity> findByStatus(String status);
}
