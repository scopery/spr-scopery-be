package com.company.scopery.modules.workspace.team.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddTeamMemberRequest(@NotNull UUID userId) {
}
