package com.company.scopery.modules.integrationhub.exportjob.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ExportJobRepository {
    ExportJob save(ExportJob j);
    Optional<ExportJob> findById(UUID id);
    List<ExportJob> findByWorkspaceId(UUID workspaceId);
}
