package com.company.scopery.modules.aiagent.tool.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BindAgentToolRequest(
        @NotNull UUID agentId
) {}
