package com.company.scopery.modules.integrationhub.importjob.domain.model;
import java.util.List; import java.util.UUID;
public interface ImportRowResultRepository {
    ImportRowResult save(ImportRowResult r);
    List<ImportRowResult> findByImportJobId(UUID importJobId);
    List<ImportRowResult> findByWorkspaceIdAndImportJobId(UUID workspaceId, UUID importJobId);
}
