package com.company.scopery.modules.servicesupport.requesttype.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportRequestTypeRepository {
    SupportRequestType save(SupportRequestType requestType);
    Optional<SupportRequestType> findById(UUID id);
    List<SupportRequestType> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndTypeCode(UUID workspaceId, String typeCode);
}
