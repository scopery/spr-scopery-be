package com.company.scopery.modules.projectbaseline.changerequestitem.application.command;
import java.util.UUID;
public record UpdateChangeRequestItemCommand(UUID projectId, UUID changeRequestId, UUID itemId, String targetType,
        UUID targetId, String operation, String summary, String beforeSnapshotJson, String afterSnapshotJson, String applyPayloadJson) {}
