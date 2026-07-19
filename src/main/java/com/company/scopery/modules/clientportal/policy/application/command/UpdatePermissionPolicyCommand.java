package com.company.scopery.modules.clientportal.policy.application.command;
import java.util.UUID;
public record UpdatePermissionPolicyCommand(UUID workspaceId, UUID policyId, String name, String description, String permissionsJson) {}
