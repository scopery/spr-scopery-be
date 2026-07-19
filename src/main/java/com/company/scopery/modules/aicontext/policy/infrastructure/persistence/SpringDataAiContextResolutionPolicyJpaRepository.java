package com.company.scopery.modules.aicontext.policy.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiContextResolutionPolicyJpaRepository
        extends JpaRepository<AiContextResolutionPolicyJpaEntity, UUID> {

    Optional<AiContextResolutionPolicyJpaEntity> findByWorkspaceIdAndPolicyCode(UUID workspaceId, String policyCode);

    List<AiContextResolutionPolicyJpaEntity> findByWorkspaceId(UUID workspaceId);
}
