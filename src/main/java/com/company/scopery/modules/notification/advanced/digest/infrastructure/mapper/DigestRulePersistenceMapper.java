package com.company.scopery.modules.notification.advanced.digest.infrastructure.mapper;
import com.company.scopery.modules.notification.advanced.digest.domain.model.DigestRule;
import com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence.DigestRuleJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DigestRulePersistenceMapper {
    public DigestRule toDomain(DigestRuleJpaEntity e) {
        return new DigestRule(e.getId(), e.getWorkspaceId(), e.getCode(), e.getName(), e.getScope(), e.getFrequency(),
                e.getScheduleConfigJson(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public DigestRuleJpaEntity toJpa(DigestRule d) {
        DigestRuleJpaEntity e = new DigestRuleJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setCode(d.code()); e.setName(d.name()); e.setScope(d.scope());
        e.setFrequency(d.frequency()); e.setScheduleConfigJson(d.scheduleConfigJson()); e.setStatus(d.status()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
