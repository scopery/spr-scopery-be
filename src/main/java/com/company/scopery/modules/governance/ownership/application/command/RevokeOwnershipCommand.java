package com.company.scopery.modules.governance.ownership.application.command;
import java.util.UUID;
public record RevokeOwnershipCommand(UUID projectId, String objectTypeCode, UUID targetId) {}
