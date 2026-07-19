package com.company.scopery.modules.servicesupport.maintenance.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenancePlan;
import com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence.MaintenancePlanJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MaintenancePlanPersistenceMapper {
    public MaintenancePlanJpaEntity toJpa(MaintenancePlan d){ MaintenancePlanJpaEntity e=new MaintenancePlanJpaEntity(); e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setName(d.name()); e.setStatus(d.status()); e.setPlannedStart(d.plannedStart()); e.setPlannedEnd(d.plannedEnd()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e; }
    public MaintenancePlan toDomain(MaintenancePlanJpaEntity e){ return new MaintenancePlan(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getName(), e.getStatus(), e.getPlannedStart(), e.getPlannedEnd(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt()); }
}
