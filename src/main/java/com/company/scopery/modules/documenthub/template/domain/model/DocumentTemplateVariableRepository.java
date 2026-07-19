package com.company.scopery.modules.documenthub.template.domain.model;

import java.util.List;
import java.util.UUID;

public interface DocumentTemplateVariableRepository {
    DocumentTemplateVariable save(DocumentTemplateVariable variable);
    List<DocumentTemplateVariable> saveAll(List<DocumentTemplateVariable> variables);
    List<DocumentTemplateVariable> findByTemplateVersionId(UUID templateVersionId);
    void deleteByTemplateVersionId(UUID templateVersionId);
}
