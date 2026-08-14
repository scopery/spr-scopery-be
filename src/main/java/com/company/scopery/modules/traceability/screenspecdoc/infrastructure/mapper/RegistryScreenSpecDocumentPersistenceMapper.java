package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.mapper;

import com.company.scopery.modules.traceability.screenspecdoc.domain.enums.RegistryScreenSpecDocStatus;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocument;
import com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence.RegistryScreenSpecDocumentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistryScreenSpecDocumentPersistenceMapper {

    public RegistryScreenSpecDocument toDomain(RegistryScreenSpecDocumentJpaEntity e) {
        return new RegistryScreenSpecDocument(
                e.getId(),
                e.getProjectId(),
                e.getWorkspaceId(),
                e.getDocumentCode(),
                e.getDocumentName(),
                e.getProjectName(),
                e.getSystemName(),
                e.getPhaseName(),
                e.getLanguage(),
                e.getOverview(),
                e.getFigmaUrl(),
                RegistryScreenSpecDocStatus.valueOf(e.getStatus()),
                e.getVersion() == null ? 0 : e.getVersion(),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public RegistryScreenSpecDocumentJpaEntity toJpaEntity(RegistryScreenSpecDocument d) {
        RegistryScreenSpecDocumentJpaEntity e = new RegistryScreenSpecDocumentJpaEntity();
        e.setId(d.id());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setDocumentCode(d.documentCode());
        e.setDocumentName(d.documentName());
        e.setProjectName(d.projectName());
        e.setSystemName(d.systemName());
        e.setPhaseName(d.phaseName());
        e.setLanguage(d.language());
        e.setOverview(d.overview());
        e.setFigmaUrl(d.figmaUrl());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
