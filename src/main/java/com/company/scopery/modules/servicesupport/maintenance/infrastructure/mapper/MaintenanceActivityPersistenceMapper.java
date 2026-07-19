package com.company.scopery.modules.servicesupport.maintenance.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceActivity;
import com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence.MaintenanceActivityJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MaintenanceActivityPersistenceMapper {
    public MaintenanceActivityJpaEntity toJpa(MaintenanceActivity d) {
        var e = new MaintenanceActivityJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setMaintenanceWindowId(d.maintenanceWindowId());
        e.setMaintenancePlanId(d.maintenancePlanId()); e.setServiceProfileId(d.serviceProfileId());
        e.setProjectId(d.projectId()); e.setActivityType(d.activityType()); e.setTitle(d.title());
        e.setDescription(d.description()); e.setOutcomeSummary(d.outcomeSummary()); e.setEffortHours(d.effortHours());
        e.setClientVisible(d.clientVisible()); e.setPerformedAt(d.performedAt()); e.setPerformedBy(d.performedBy());
        e.setCreatedAt(d.createdAt());
        return e;
    }
    public MaintenanceActivity toDomain(MaintenanceActivityJpaEntity e) {
        return new MaintenanceActivity(e.getId(), e.getWorkspaceId(), e.getMaintenanceWindowId(), e.getMaintenancePlanId(),
                e.getServiceProfileId(), e.getProjectId(), e.getActivityType(), e.getTitle(), e.getDescription(),
                e.getOutcomeSummary(), e.getEffortHours(), e.isClientVisible(),
                e.getPerformedAt(), e.getPerformedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
