package com.company.scopery.modules.workspace.context.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SwitchWorkspaceRequest(@NotNull UUID workspaceId) {}
