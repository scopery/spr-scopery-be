package com.company.scopery.modules.governance.lock.application.command;
import java.util.UUID;
public record CreateObjectLockCommand(UUID projectId, String objectTypeCode, UUID targetId, String lockType, String reason) {}
