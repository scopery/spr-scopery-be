package com.company.scopery.modules.project.wbs.domain.model;

import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;

import java.time.Instant;
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
                WbsNodeStatus.ACTIVE,
                0,
                null,
                null
        );
    }

    public WbsNode update(String title, String description, WbsNodeType nodeType) {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, this.parentId,
                this.code, title, description, nodeType,
                this.level, this.path, this.sortOrder,
                this.status, this.version, this.createdAt, this.updatedAt
        );
    }

    public WbsNode move(UUID newParentId, int newSortOrder, int newLevel, String newPath) {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, newParentId,
                this.code, this.title, this.description, this.nodeType,
                newLevel, newPath, newSortOrder,
                this.status, this.version, this.createdAt, this.updatedAt
        );
    }

    public WbsNode archive() {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, this.parentId,
                this.code, this.title, this.description, this.nodeType,
                this.level, this.path, this.sortOrder,
                WbsNodeStatus.ARCHIVED, this.version, this.createdAt, this.updatedAt
        );
    }

    public WbsNode withPath(String newPath, int newLevel) {
        return new WbsNode(
                this.id, this.projectId, this.projectPhaseId, this.parentId,
                this.code, this.title, this.description, this.nodeType,
                newLevel, newPath, this.sortOrder,
                this.status, this.version, this.createdAt, this.updatedAt
        );
    }
}
