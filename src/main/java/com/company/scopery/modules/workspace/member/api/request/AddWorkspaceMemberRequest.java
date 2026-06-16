package com.company.scopery.modules.workspace.member.api.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddWorkspaceMemberRequest(@NotNull UUID userId) {
}
