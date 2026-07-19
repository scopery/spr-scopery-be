package com.company.scopery.modules.notification.advanced.alert.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.alert.domain.model.AlertRule;
import com.company.scopery.modules.notification.advanced.alert.infrastructure.persistence.AlertRuleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class AlertRulePersistenceMapper {
    public AlertRule toDomain(AlertRuleJpaEntity e) {
        return new AlertRule(e.getId(), e.getWorkspaceId(), e.getRuleCode(), e.getName(), e.getCategory(), e.getConditionJson(),
                e.isBypassQuietHours(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public AlertRuleJpaEntity toJpa(AlertRule d) {
        AlertRuleJpaEntity e = new AlertRuleJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setRuleCode(d.ruleCode()); e.setName(d.name()); e.setCategory(d.category());
        e.setConditionJson(d.conditionJson()); e.setBypassQuietHours(d.bypassQuietHours()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
