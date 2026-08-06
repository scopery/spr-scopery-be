package com.company.scopery.modules.specpack.agentsession.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartAgentSessionRequest(
        @NotNull UUID specPackId,
        @NotNull UUID scopePackageId
) {}
