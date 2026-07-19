package com.company.scopery.modules.servicesupport.supportcase.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportCaseRepository {
    SupportCase save(SupportCase c);
    Optional<SupportCase> findById(UUID id);
    List<SupportCase> findByWorkspaceId(UUID workspaceId);
    List<SupportCase> findByProjectId(UUID projectId);
}
