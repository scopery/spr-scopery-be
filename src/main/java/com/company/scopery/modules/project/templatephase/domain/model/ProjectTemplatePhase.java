package com.company.scopery.modules.project.templatephase.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplatePhase(
        UUID id,
        UUID templateVersionId,
        UUID phaseDefinitionId,
        String code,
        String name,
        String description,
        int displayOrder,
        Integer defaultDurationDays,
        Integer startOffsetDays,
        UUID deliverableDocumentTypeId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectTemplatePhase create(
            UUID templateVersionId,
            UUID phaseDefinitionId,
            String code,
            String name,
            String description,
            int displayOrder,
            Integer defaultDurationDays,
            Integer startOffsetDays,
            UUID deliverableDocumentTypeId) {
        return new ProjectTemplatePhase(
                UUID.randomUUID(),
                templateVersionId,
                phaseDefinitionId,
                code,
                name,
                description,
                displayOrder,
                defaultDurationDays,
                startOffsetDays,
                deliverableDocumentTypeId,
                0,
                null,
                null
        );
    }

    public ProjectTemplatePhase update(
            String code,
            String name,
            String description,
            int displayOrder,
            Integer defaultDurationDays,
            Integer startOffsetDays,
            UUID deliverableDocumentTypeId,
            UUID phaseDefinitionId) {
        return new ProjectTemplatePhase(
                this.id,
                this.templateVersionId,
                phaseDefinitionId,
                code,
                name,
                description,
                displayOrder,
                defaultDurationDays,
                startOffsetDays,
                deliverableDocumentTypeId,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }

    public ProjectTemplatePhase withDisplayOrder(int displayOrder) {
        return new ProjectTemplatePhase(
                this.id, this.templateVersionId, this.phaseDefinitionId,
                this.code, this.name, this.description, displayOrder,
                this.defaultDurationDays, this.startOffsetDays,
                this.deliverableDocumentTypeId, this.version,
                this.createdAt, this.updatedAt
        );
    }
}
