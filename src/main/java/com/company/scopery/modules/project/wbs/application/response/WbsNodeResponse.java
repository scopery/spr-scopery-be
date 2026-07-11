package com.company.scopery.modules.project.wbs.application.response;

import com.company.scopery.modules.project.wbs.domain.model.WbsNode;

import java.time.Instant;
import java.util.UUID;

public record WbsNodeResponse(
        UUID id,
        UUID projectId,
        UUID projectPhaseId,
        UUID parentId,
        String code,
        String title,
        String description,
        String nodeType,
        int level,
        String path,
        int sortOrder,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static WbsNodeResponse from(WbsNode n) {
        return new WbsNodeResponse(
                n.id(),
                n.projectId(),
                n.projectPhaseId(),
                n.parentId(),
                n.code(),
                n.title(),
                n.description(),
                n.nodeType().name(),
                n.level(),
                n.path(),
                n.sortOrder(),
                n.status().name(),
                n.version(),
                n.createdAt(),
                n.updatedAt()
        );
    }
}
