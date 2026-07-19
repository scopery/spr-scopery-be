package com.company.scopery.modules.servicesupport.handover.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackage;
import com.company.scopery.modules.servicesupport.handover.infrastructure.persistence.ServiceHandoverPackageJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ServiceHandoverPackagePersistenceMapper {
    public ServiceHandoverPackageJpaEntity toJpa(ServiceHandoverPackage d) {
        var e = new ServiceHandoverPackageJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId());
        e.setServiceProfileId(d.serviceProfileId()); e.setPackageCode(d.packageCode()); e.setTitle(d.title());
        e.setSummary(d.summary()); e.setStatus(d.status()); e.setClientVisible(d.clientVisible());
        e.setFinalizedAt(d.finalizedAt()); e.setFinalizedBy(d.finalizedBy()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ServiceHandoverPackage toDomain(ServiceHandoverPackageJpaEntity e) {
        return new ServiceHandoverPackage(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getServiceProfileId(),
                e.getPackageCode(), e.getTitle(), e.getSummary(), e.getStatus(), e.isClientVisible(),
                e.getFinalizedAt(), e.getFinalizedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
