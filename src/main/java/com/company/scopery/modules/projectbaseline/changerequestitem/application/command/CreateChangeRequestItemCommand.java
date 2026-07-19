package com.company.scopery.modules.projectbaseline.changerequestitem.application.command;
import java.util.UUID;
public record CreateChangeRequestItemCommand(UUID projectId, UUID changeRequestId, String targetType, UUID targetId,
        String operation, String summary, String beforeSnapshotJson, String afterSnapshotJson, String applyPayloadJson) {}
