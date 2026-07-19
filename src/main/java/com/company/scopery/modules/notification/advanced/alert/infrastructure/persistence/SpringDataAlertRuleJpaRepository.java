package com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataAlertRuleJpaRepository extends JpaRepository<AlertRuleJpaEntity, UUID> {
    Optional<AlertRuleJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<AlertRuleJpaEntity> findByWorkspaceIdOrderByRuleCodeAsc(UUID workspaceId);
    List<AlertRuleJpaEntity> findByStatus(String status);
}
