package com.company.scopery.modules.elicitation.session.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartSessionRequest(
        @NotNull UUID scopePackageId,
        String title,
        String language
) {}
