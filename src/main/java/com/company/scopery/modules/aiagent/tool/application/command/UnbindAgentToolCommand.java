package com.company.scopery.modules.aiagent.tool.application.command;

import java.util.UUID;

public record UnbindAgentToolCommand(UUID toolId, UUID agentId) {}
