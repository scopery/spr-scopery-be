package com.company.scopery.modules.servicesupport.costinput.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ServiceCostInputRepository {
    ServiceCostInput save(ServiceCostInput input);
    Optional<ServiceCostInput> findById(UUID id);
    List<ServiceCostInput> findByWorkspaceId(UUID workspaceId);
    List<ServiceCostInput> findBySupportCaseId(UUID supportCaseId);
}
