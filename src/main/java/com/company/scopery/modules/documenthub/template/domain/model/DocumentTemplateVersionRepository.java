package com.company.scopery.modules.documenthub.template.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface DocumentTemplateVersionRepository {
    DocumentTemplateVersion save(DocumentTemplateVersion version);
    Optional<DocumentTemplateVersion> findByIdAndTemplateId(UUID id, UUID templateId);
}
