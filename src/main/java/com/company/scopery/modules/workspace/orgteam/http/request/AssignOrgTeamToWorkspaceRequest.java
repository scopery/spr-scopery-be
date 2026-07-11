package com.company.scopery.modules.workspace.orgteam.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignOrgTeamToWorkspaceRequest(@NotNull UUID workspaceId) {
}
