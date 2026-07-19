package com.company.scopery.modules.servicesupport.handover.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ServiceHandoverPackageRepository {
    ServiceHandoverPackage save(ServiceHandoverPackage pack);
    Optional<ServiceHandoverPackage> findById(UUID id);
    List<ServiceHandoverPackage> findByWorkspaceId(UUID workspaceId);
}
