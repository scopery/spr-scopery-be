package com.company.scopery.modules.clientportal.grant.application.command;
import java.time.Instant; import java.util.UUID;
public record CreatePortalAccessGrantCommand(UUID projectId, UUID portalAccountId, String permissionPolicyCode, Instant expiresAt) {}
