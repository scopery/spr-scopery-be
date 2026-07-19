package com.company.scopery.modules.servicesupport.sla.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.sla.domain.model.*;
import com.company.scopery.modules.servicesupport.sla.infrastructure.persistence.*;
import org.springframework.stereotype.Component;
@Component
public class SlaPersistenceMapper {
    public SlaPolicyJpaEntity toJpa(SlaPolicy d){ SlaPolicyJpaEntity e=new SlaPolicyJpaEntity(); e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setPolicyCode(d.policyCode()); e.setName(d.name()); e.setFirstResponseMinutes(d.firstResponseMinutes()); e.setResolveMinutes(d.resolveMinutes()); e.setBusinessHoursOnly(d.businessHoursOnly()); e.setStatus(d.status()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e; }
    public SlaPolicy toDomain(SlaPolicyJpaEntity e){ return new SlaPolicy(e.getId(), e.getWorkspaceId(), e.getPolicyCode(), e.getName(), e.getFirstResponseMinutes(), e.getResolveMinutes(), Boolean.TRUE.equals(e.getBusinessHoursOnly()), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt()); }
    public SlaClockJpaEntity toJpa(SlaClock d){ SlaClockJpaEntity e=new SlaClockJpaEntity(); e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId()); e.setSlaPolicyId(d.slaPolicyId()); e.setClockType(d.clockType()); e.setStartedAt(d.startedAt()); e.setDueAt(d.dueAt()); e.setPausedAt(d.pausedAt()); e.setCompletedAt(d.completedAt()); e.setStatus(d.status()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e; }
    public SlaClock toDomain(SlaClockJpaEntity e){ return new SlaClock(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getSlaPolicyId(), e.getClockType(), e.getStartedAt(), e.getDueAt(), e.getPausedAt(), e.getCompletedAt(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt()); }
    public SlaBreachJpaEntity toJpa(SlaBreach d){ SlaBreachJpaEntity e=new SlaBreachJpaEntity(); e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId()); e.setSlaClockId(d.slaClockId()); e.setBreachType(d.breachType()); e.setBreachedAt(d.breachedAt()); e.setStatus(d.status()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e; }
    public SlaBreach toDomain(SlaBreachJpaEntity e){ return new SlaBreach(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getSlaClockId(), e.getBreachType(), e.getBreachedAt(), e.getStatus(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt()); }
}
