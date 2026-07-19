package com.company.scopery.modules.servicesupport.escalation.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportEscalationRuleJpaRepository extends JpaRepository<SupportEscalationRuleJpaEntity, UUID> {
    List<SupportEscalationRuleJpaEntity> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndRuleCode(UUID workspaceId, String ruleCode);
}
