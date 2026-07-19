package com.company.scopery.modules.project.templatetask.application.response;

import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateTaskResponse(
        UUID id,
        UUID templateVersionId,
        UUID templatePhaseId,
        UUID templateWbsNodeId,
        String code,
        String title,
        String description,
        String defaultPriority,
        BigDecimal estimateHours,
        Integer dueOffsetDays,
        Integer startOffsetDays,
        String defaultAssigneeRoleCode,
        UUID deliverableDocumentTypeId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectTemplateTaskResponse from(ProjectTemplateTask t) {
        return new ProjectTemplateTaskResponse(
                t.id(), t.templateVersionId(), t.templatePhaseId(), t.templateWbsNodeId(),
                t.code(), t.title(), t.description(),
                t.defaultPriority() != null ? t.defaultPriority().name() : null,
                t.estimateHours(), t.dueOffsetDays(), t.startOffsetDays(),
                t.defaultAssigneeRoleCode(), t.deliverableDocumentTypeId(),
                t.version(), t.createdAt(), t.updatedAt()
        );
    }
}
