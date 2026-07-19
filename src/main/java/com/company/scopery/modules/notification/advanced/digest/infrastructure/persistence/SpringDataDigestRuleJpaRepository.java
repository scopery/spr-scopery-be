package com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataDigestRuleJpaRepository extends JpaRepository<DigestRuleJpaEntity, UUID> {
    Optional<DigestRuleJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<DigestRuleJpaEntity> findByWorkspaceIdOrderByCodeAsc(UUID workspaceId);
    List<DigestRuleJpaEntity> findByStatus(String status);
}
