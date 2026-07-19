package com.company.scopery.modules.servicesupport.sla.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaTarget;
import com.company.scopery.modules.servicesupport.sla.infrastructure.persistence.SlaTargetJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SlaTargetPersistenceMapper {
    public SlaTargetJpaEntity toJpa(SlaTarget d) {
        var e = new SlaTargetJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSlaPolicyId(d.slaPolicyId());
        e.setRequestTypeId(d.requestTypeId()); e.setPriority(d.priority()); e.setTargetType(d.targetType());
        e.setDurationMinutes(d.durationMinutes()); e.setEnabled(d.enabled()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SlaTarget toDomain(SlaTargetJpaEntity e) {
        return new SlaTarget(e.getId(), e.getWorkspaceId(), e.getSlaPolicyId(), e.getRequestTypeId(),
                e.getPriority(), e.getTargetType(), e.getDurationMinutes(), e.isEnabled(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
