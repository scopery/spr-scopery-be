package com.company.scopery.modules.governance.versioning.application.command;
import java.util.UUID;
public record CreateVersionSnapshotCommand(UUID projectId, String objectTypeCode, UUID targetId, String snapshotJson, String changeReason) {}
