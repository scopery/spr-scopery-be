package com.company.scopery.modules.integrationhub.importjob.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ImportJobRepository {
    ImportJob save(ImportJob j);
    Optional<ImportJob> findById(UUID id);
    List<ImportJob> findByWorkspaceId(UUID workspaceId);
}
