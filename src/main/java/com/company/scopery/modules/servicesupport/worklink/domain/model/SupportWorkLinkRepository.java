package com.company.scopery.modules.servicesupport.worklink.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportWorkLinkRepository {
    SupportWorkLink save(SupportWorkLink link);
    Optional<SupportWorkLink> findById(UUID id);
    List<SupportWorkLink> findBySupportCaseId(UUID caseId);
    List<SupportWorkLink> findByWorkspaceId(UUID workspaceId);
}
