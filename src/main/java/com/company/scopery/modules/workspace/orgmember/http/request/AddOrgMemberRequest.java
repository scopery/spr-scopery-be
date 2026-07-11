package com.company.scopery.modules.workspace.orgmember.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddOrgMemberRequest(
        @NotNull UUID userId,
        String membershipType) {
}
