package com.company.scopery.modules.project.templatewbs.application.response;

import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateWbsNodeResponse(
        UUID id,
        UUID templateVersionId,
        UUID parentId,
        UUID templatePhaseId,
        String code,
        String title,
        String description,
        String nodeType,
        int depth,
        int orderIndex,
        UUID deliverableDocumentTypeId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectTemplateWbsNodeResponse from(ProjectTemplateWbsNode n) {
        return new ProjectTemplateWbsNodeResponse(
                n.id(), n.templateVersionId(), n.parentId(), n.templatePhaseId(),
                n.code(), n.title(), n.description(),
                n.nodeType() != null ? n.nodeType().name() : null,
                n.depth(), n.orderIndex(), n.deliverableDocumentTypeId(),
                n.version(), n.createdAt(), n.updatedAt()
        );
    }
}
