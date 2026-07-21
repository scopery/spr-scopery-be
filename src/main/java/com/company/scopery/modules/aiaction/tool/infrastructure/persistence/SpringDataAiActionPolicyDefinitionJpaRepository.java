package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionPolicyDefinitionJpaRepository extends JpaRepository<AiActionPolicyDefinitionJpaEntity, UUID> {

    Optional<AiActionPolicyDefinitionJpaEntity> findByPolicyCodeAndPolicyVersion(String policyCode, int policyVersion);

    Optional<AiActionPolicyDefinitionJpaEntity> findByPolicyCodeAndStatus(String policyCode, String status);
}
