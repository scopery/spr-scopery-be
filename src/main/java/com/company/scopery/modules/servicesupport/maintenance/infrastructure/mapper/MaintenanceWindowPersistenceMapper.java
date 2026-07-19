package com.company.scopery.modules.servicesupport.maintenance.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceWindow;
import com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence.MaintenanceWindowJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MaintenanceWindowPersistenceMapper {
    public MaintenanceWindowJpaEntity toJpa(MaintenanceWindow d) {
        var e = new MaintenanceWindowJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setMaintenancePlanId(d.maintenancePlanId());
        e.setTitle(d.title()); e.setWindowStart(d.windowStart()); e.setWindowEnd(d.windowEnd());
        e.setStatus(d.status()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public MaintenanceWindow toDomain(MaintenanceWindowJpaEntity e) {
        return new MaintenanceWindow(e.getId(), e.getWorkspaceId(), e.getMaintenancePlanId(), e.getTitle(),
                e.getWindowStart(), e.getWindowEnd(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
