package com.company.scopery.modules.aiagent.tool.application.command;

import java.util.UUID;

public record AddAiToolPermissionCommand(UUID toolId, String permissionCode, String description) {}
