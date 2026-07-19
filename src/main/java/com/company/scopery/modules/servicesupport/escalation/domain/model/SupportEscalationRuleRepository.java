package com.company.scopery.modules.servicesupport.escalation.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportEscalationRuleRepository {
    SupportEscalationRule save(SupportEscalationRule rule);
    Optional<SupportEscalationRule> findById(UUID id);
    List<SupportEscalationRule> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndRuleCode(UUID workspaceId, String ruleCode);
}
