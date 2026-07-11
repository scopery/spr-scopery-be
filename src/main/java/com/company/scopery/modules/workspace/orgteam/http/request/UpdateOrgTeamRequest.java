package com.company.scopery.modules.workspace.orgteam.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrgTeamRequest(
        @NotBlank String name,
        String description) {
}
