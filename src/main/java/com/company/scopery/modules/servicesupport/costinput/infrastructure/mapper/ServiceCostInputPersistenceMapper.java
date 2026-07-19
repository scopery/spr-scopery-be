package com.company.scopery.modules.servicesupport.costinput.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInput;
import com.company.scopery.modules.servicesupport.costinput.infrastructure.persistence.ServiceCostInputJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ServiceCostInputPersistenceMapper {
    public ServiceCostInputJpaEntity toJpa(ServiceCostInput d) {
        var e = new ServiceCostInputJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setServiceProfileId(d.serviceProfileId()); e.setSupportCaseId(d.supportCaseId());
        e.setIncidentId(d.incidentId()); e.setMaintenanceActivityId(d.maintenanceActivityId());
        e.setResourceProfileId(d.resourceProfileId()); e.setSourceType(d.sourceType()); e.setSourceId(d.sourceId());
        e.setEffortHours(d.effortHours()); e.setRateAmount(d.rateAmount()); e.setCurrency(d.currency());
        e.setCostAmount(d.costAmount()); e.setStatus(d.status()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ServiceCostInput toDomain(ServiceCostInputJpaEntity e) {
        return new ServiceCostInput(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getServiceProfileId(),
                e.getSupportCaseId(), e.getIncidentId(), e.getMaintenanceActivityId(), e.getResourceProfileId(),
                e.getSourceType(), e.getSourceId(), e.getEffortHours(), e.getRateAmount(), e.getCurrency(),
                e.getCostAmount(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
