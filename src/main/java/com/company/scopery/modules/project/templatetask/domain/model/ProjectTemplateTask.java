package com.company.scopery.modules.project.templatetask.domain.model;

import com.company.scopery.modules.project.task.domain.enums.TaskPriority;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectTemplateTask(
        UUID id,
        UUID templateVersionId,
        UUID templatePhaseId,
        UUID templateWbsNodeId,
        String code,
        String title,
        String description,
        TaskPriority defaultPriority,
        BigDecimal estimateHours,
        Integer dueOffsetDays,
        Integer startOffsetDays,
        String defaultAssigneeRoleCode,
        UUID deliverableDocumentTypeId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProjectTemplateTask create(
            UUID templateVersionId,
            UUID templatePhaseId,
            UUID templateWbsNodeId,
            String code,
            String title,
            String description,
            TaskPriority defaultPriority,
            BigDecimal estimateHours,
            Integer dueOffsetDays,
            Integer startOffsetDays,
            String defaultAssigneeRoleCode,
            UUID deliverableDocumentTypeId) {
        return new ProjectTemplateTask(
                UUID.randomUUID(),
                templateVersionId,
                templatePhaseId,
                templateWbsNodeId,
                code,
                title,
                description,
                defaultPriority,
                estimateHours,
                dueOffsetDays,
                startOffsetDays,
                defaultAssigneeRoleCode,
                deliverableDocumentTypeId,
                0,
                null,
                null
        );
    }

    public ProjectTemplateTask update(
            UUID templatePhaseId,
            UUID templateWbsNodeId,
            String code,
            String title,
            String description,
            TaskPriority defaultPriority,
            BigDecimal estimateHours,
            Integer dueOffsetDays,
            Integer startOffsetDays,
            String defaultAssigneeRoleCode,
            UUID deliverableDocumentTypeId) {
        return new ProjectTemplateTask(
                this.id,
                this.templateVersionId,
                templatePhaseId,
                templateWbsNodeId,
                code,
                title,
                description,
                defaultPriority,
                estimateHours,
                dueOffsetDays,
                startOffsetDays,
                defaultAssigneeRoleCode,
                deliverableDocumentTypeId,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }
}
