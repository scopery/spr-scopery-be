package com.company.scopery.modules.workspace.orgteam.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrgTeamRequest(
        @NotBlank String name,
        @NotBlank @Size(max = 100) String code,
        String description) {
}
