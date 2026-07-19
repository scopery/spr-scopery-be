package com.company.scopery.modules.clientportal.grant.application.command;
import java.util.UUID;
public record RevokePortalAccessGrantCommand(UUID projectId, UUID grantId) {}
