package com.company.scopery.modules.workspace.team.api.request;

public record SearchTeamRequest(
        String keyword,
        String status,
        int page,
        int size) {
}
