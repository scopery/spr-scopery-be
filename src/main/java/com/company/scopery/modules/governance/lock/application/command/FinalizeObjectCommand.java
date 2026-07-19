package com.company.scopery.modules.governance.lock.application.command;
import java.util.UUID;
public record FinalizeObjectCommand(UUID projectId, String objectTypeCode, UUID targetId, String reason) {}
