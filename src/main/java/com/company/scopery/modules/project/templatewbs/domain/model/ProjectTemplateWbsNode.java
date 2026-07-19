package com.company.scopery.modules.project.templatewbs.domain.model;

import com.company.scopery.modules.project.templatewbs.domain.enums.TemplateWbsNodeType;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateWbsNode(
        UUID id,
        UUID templateVersionId,
        UUID parentId,
        UUID templatePhaseId,
        String code,
        String title,
        String description,
        TemplateWbsNodeType nodeType,
        int depth,
        int orderIndex,
        UUID deliverableDocumentTypeId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectTemplateWbsNode create(
            UUID templateVersionId,
            UUID parentId,
            UUID templatePhaseId,
            String code,
            String title,
            String description,
            TemplateWbsNodeType nodeType,
            int depth,
            int orderIndex,
            UUID deliverableDocumentTypeId) {
        return new ProjectTemplateWbsNode(
                UUID.randomUUID(),
                templateVersionId,
                parentId,
                templatePhaseId,
                code,
                title,
                description,
                nodeType,
                depth,
                orderIndex,
                deliverableDocumentTypeId,
                0,
                null,
                null
        );
    }

    public ProjectTemplateWbsNode update(
            String code,
            String title,
            String description,
            TemplateWbsNodeType nodeType,
            UUID templatePhaseId,
            UUID deliverableDocumentTypeId) {
        return new ProjectTemplateWbsNode(
                this.id, this.templateVersionId, this.parentId, templatePhaseId,
                code, title, description, nodeType,
                this.depth, this.orderIndex, deliverableDocumentTypeId,
                this.version, this.createdAt, this.updatedAt
        );
    }

    public ProjectTemplateWbsNode move(UUID newParentId, int newDepth, int newOrderIndex) {
        return new ProjectTemplateWbsNode(
                this.id, this.templateVersionId, newParentId, this.templatePhaseId,
                this.code, this.title, this.description, this.nodeType,
                newDepth, newOrderIndex, this.deliverableDocumentTypeId,
                this.version, this.createdAt, this.updatedAt
        );
    }
}
