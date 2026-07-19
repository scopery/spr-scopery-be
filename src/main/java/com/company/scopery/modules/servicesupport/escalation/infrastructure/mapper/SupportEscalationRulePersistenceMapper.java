package com.company.scopery.modules.servicesupport.escalation.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.escalation.domain.model.SupportEscalationRule;
import com.company.scopery.modules.servicesupport.escalation.infrastructure.persistence.SupportEscalationRuleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportEscalationRulePersistenceMapper {
    public SupportEscalationRuleJpaEntity toJpa(SupportEscalationRule d) {
        var e = new SupportEscalationRuleJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setServiceProfileId(d.serviceProfileId());
        e.setQueueId(d.queueId()); e.setRuleCode(d.ruleCode()); e.setName(d.name());
        e.setTriggerType(d.triggerType()); e.setEnabled(d.enabled()); e.setStatus(d.status());
        e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportEscalationRule toDomain(SupportEscalationRuleJpaEntity e) {
        return new SupportEscalationRule(e.getId(), e.getWorkspaceId(), e.getServiceProfileId(), e.getQueueId(),
                e.getRuleCode(), e.getName(), e.getTriggerType(), e.isEnabled(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
