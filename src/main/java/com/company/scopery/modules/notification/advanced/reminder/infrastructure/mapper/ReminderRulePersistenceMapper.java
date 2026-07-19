package com.company.scopery.modules.notification.advanced.reminder.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.reminder.domain.model.ReminderRule;
import com.company.scopery.modules.notification.advanced.reminder.infrastructure.persistence.ReminderRuleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ReminderRulePersistenceMapper {
    public ReminderRule toDomain(ReminderRuleJpaEntity e) {
        return new ReminderRule(e.getId(), e.getWorkspaceId(), e.getRuleCode(), e.getName(),
                e.getConditionJson(), e.getRecipientRuleJson(), e.getStatus(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public ReminderRuleJpaEntity toJpa(ReminderRule d) {
        var e = new ReminderRuleJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setRuleCode(d.ruleCode()); e.setName(d.name());
        e.setConditionJson(d.conditionJson()); e.setRecipientRuleJson(d.recipientRuleJson());
        e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
