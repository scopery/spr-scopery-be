package com.company.scopery.modules.project.templatephase.application.response;

import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;

import java.time.Instant;
import java.util.UUID;

public record ProjectTemplatePhaseResponse(
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
    public static ProjectTemplatePhaseResponse from(ProjectTemplatePhase p) {
        return new ProjectTemplatePhaseResponse(
                p.id(), p.templateVersionId(), p.phaseDefinitionId(),
                p.code(), p.name(), p.description(), p.displayOrder(),
                p.defaultDurationDays(), p.startOffsetDays(),
                p.deliverableDocumentTypeId(), p.version(),
                p.createdAt(), p.updatedAt()
        );
    }
}
