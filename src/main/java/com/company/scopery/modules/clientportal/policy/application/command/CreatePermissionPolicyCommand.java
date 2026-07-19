package com.company.scopery.modules.clientportal.policy.application.command;
import java.util.UUID;
public record CreatePermissionPolicyCommand(UUID workspaceId, String code, String name, String description, String permissionsJson) {}
