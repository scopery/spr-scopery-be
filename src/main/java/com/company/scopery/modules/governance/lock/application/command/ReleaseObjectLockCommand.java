package com.company.scopery.modules.governance.lock.application.command;
import java.util.UUID;
public record ReleaseObjectLockCommand(UUID projectId, UUID lockId) {}
