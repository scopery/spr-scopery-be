package com.company.scopery.modules.integrationhub.importtemplate.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ImportTemplateRepository {
    ImportTemplate save(ImportTemplate t);
    Optional<ImportTemplate> findById(UUID id);
    List<ImportTemplate> findByWorkspaceIdOrGlobal(UUID workspaceId);
    List<ImportTemplate> findAll();
}
