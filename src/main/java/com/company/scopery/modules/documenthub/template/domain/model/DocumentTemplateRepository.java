package com.company.scopery.modules.documenthub.template.domain.model;
import java.util.*;
public interface DocumentTemplateRepository {
    DocumentTemplate save(DocumentTemplate entity);
    Optional<DocumentTemplate> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<DocumentTemplate> findByWorkspaceId(UUID workspaceId);
}
