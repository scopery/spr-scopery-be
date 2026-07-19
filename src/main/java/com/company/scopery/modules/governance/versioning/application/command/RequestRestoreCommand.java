package com.company.scopery.modules.governance.versioning.application.command;
import java.util.UUID;
public record RequestRestoreCommand(UUID projectId, String objectTypeCode, UUID targetId, UUID restoreFromVersionRecordId, String reason) {}
