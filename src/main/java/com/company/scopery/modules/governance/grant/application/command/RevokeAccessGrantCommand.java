package com.company.scopery.modules.governance.grant.application.command;
import java.util.UUID;
public record RevokeAccessGrantCommand(UUID projectId, UUID grantId) {}
