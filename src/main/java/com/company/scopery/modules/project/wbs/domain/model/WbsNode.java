package com.company.scopery.modules.project.wbs.domain.model;

import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WbsNode(
        UUID id,
        UUID projectId,
        UUID projectPhaseId,
        UUID parentId,
        String code,
        String title,
        String description,
        WbsNodeType nodeType,
        int level,
        String path,
        int sortOrder,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        WbsNodeStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static WbsNode create(
            UUID projectId,
            UUID projectPhaseId,
            UUID parentId,
            String code,
            String title,
            String description,
            WbsNodeType nodeType,
            int level,
            String path,
            int sortOrder) {
        return create(
                projectId, projectPhaseId, parentId, code, title, description, nodeType,
                level, path, sortOrder, null, null);
    }

    public static WbsNode create(
            UUID projectId,
            UUID projectPhaseId,
            UUID parentId,
            String code,
            String title,
            String description,
            WbsNodeType nodeType,
            int level,
            String path,
            int sortOrder,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new WbsNode(
                UUID.randomUUID(),
                projectId,
                projectPhaseId,
                parentId,
                code,
                title,
                description,
                nodeType,
                level,
                path,
                sortOrder,
                plannedStartDate,
                plannedEndDate,
                WbsNodeStatus.ACTIVE,
                0,
                null,
                null
        );
    }

    public WbsNode update(
            String title,
            String description,
            WbsNodeType nodeType,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate) {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, this.parentId,
                this.code, title, description, nodeType,
                this.level, this.path, this.sortOrder,
                plannedStartDate, plannedEndDate,
                this.status, this.version, this.createdAt, this.updatedAt
        );
    }

    public WbsNode move(UUID newParentId, int newSortOrder, int newLevel, String newPath) {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, newParentId,
                this.code, this.title, this.description, this.nodeType,
                newLevel, newPath, newSortOrder,
                this.plannedStartDate, this.plannedEndDate,
                this.status, this.version, this.createdAt, this.updatedAt
        );
    }

    public WbsNode archive() {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, this.parentId,
                this.code, this.title, this.description, this.nodeType,
                this.level, this.path, this.sortOrder,
                this.plannedStartDate, this.plannedEndDate,
                WbsNodeStatus.ARCHIVED, this.version, this.createdAt, this.updatedAt
        );
    }

    public WbsNode withPath(String newPath, int newLevel) {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, this.parentId,
                this.code, this.title, this.description, this.nodeType,
                newLevel, newPath, this.sortOrder,
                this.plannedStartDate, this.plannedEndDate,
                this.status, this.version, this.createdAt, this.updatedAt
        );
    }
}
